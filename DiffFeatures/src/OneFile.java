import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.VirtualFile;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class OneFile implements Callable<ArrayList<Object[]>> {

    private File file1, file2;
    private ArrayList<String> content1, content2;

    public OneFile(File file1, File file2) {
        this.file1 = file1;
        this.file2 = file2;
    }

    @Override
    public ArrayList<Object[]> call() throws Exception {

        ArrayList<Object[]> result = new ArrayList<>();

        content1 = new ArrayList<>();
        content2 = new ArrayList<>();

        CtModel model1 = getModel(file1, content1);
        ArrayList<CtMethod> list1 = new ArrayList<>(model1.getElements(new TypeFilter<>(CtMethod.class)));
        HashMap<String, CtMethod> methodMap1 = new HashMap<>();
        HashMap<CtMethod, Integer> positionMap = new HashMap<>();
        int position = 0;
        while (position < list1.size()) {
            methodMap1.put(list1.get(position).getSignature(), list1.get(position));
            positionMap.put(list1.get(position), position);
            position += list1.get(position).getElements(new TypeFilter<>(CtMethod.class)).size();
        }

        CtModel model2 = getModel(file2, content2);
        list1 = new ArrayList<>(model2.getElements(new TypeFilter<>(CtMethod.class)));
        position = 0;
        while (position < list1.size()) {
            String sign = list1.get(position).getSignature();
            CtMethod method = list1.get(position);
            if (methodMap1.containsKey(sign) && !methodMap1.get(sign).equals(method)) {
                OneMethod oneMethod = new OneMethod(methodMap1.get(sign), method, content1, content2);
                double[] record = oneMethod.build();

                if (record != null) {
                    result.add(
                            new Object[]{
                                    file1.getAbsolutePath(),
                                    file2.getAbsolutePath(),
                                    record
                            });
                }

            }
            position += list1.get(position).getElements(new TypeFilter<>(CtMethod.class)).size();
        }

        return result;
    }

    private CtModel getModel(File file, ArrayList<String> list) {
        try {

            Scanner scanner = new Scanner(file);

            StringBuffer buffer = new StringBuffer();
            String s;
            while (scanner.hasNext()) {
                s = scanner.nextLine();
                list.add(s);
                buffer.append(s + "\n");
            }
            scanner.close();

            Factory factory = new spoon.reflect.factory.FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
            factory.getEnvironment().setLevel("error");
            factory.getEnvironment().setAutoImports(false);
            factory.getEnvironment().setNoClasspath(true);
            JDTBasedSpoonCompiler modelBuilder = new JDTBasedSpoonCompiler(factory);
            modelBuilder.addInputSource(new VirtualFile(buffer.toString()));
            modelBuilder.build();
            CtModel model1 = factory.getModel();

            return model1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

