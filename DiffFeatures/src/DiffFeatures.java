import spoon.support.compiler.VirtualFile;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiffFeatures {
    public static void main(String args[]) {
        try {
            DiffFeatures df = new DiffFeatures();
            df.setInput(new File("D:\\test"), new File("D:\\test"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int parallel;
    private ArrayList<Object[]> result;
    private ArrayList<File> originalFileList , changedFileList;
    private ExecutorService threadPool;
    static int FeatureNumber=95;

    public DiffFeatures(){
        parallel=1;
        result=new ArrayList<>();
        originalFileList=new ArrayList<>();
        changedFileList=new ArrayList<>();
    }

    public void setParallel(int threadNumber){
        if(threadNumber<1)
            System.out.println("Can not less than 1");
        else
            parallel=threadNumber;
    }

    public void setInput (File file1 , File file2) throws Exception{
        if(file1.isDirectory()){
            traverseFold(file1 , originalFileList);
            traverseFold(file2 , changedFileList);
        }

        else{
            originalFileList.add(file1);
            changedFileList.add(file2);
        }
    }

    public void setInput(String code1 , String code2) throws Exception{
        originalFileList.add(File.createTempFile(String.valueOf(code1.hashCode()),".java"));
        changedFileList.add(File.createTempFile(String.valueOf(code2.hashCode()),".java"));
    }

    public void build() throws Exception{

        threadPool = Executors.newFixedThreadPool(parallel);
        ExecutorCompletionService<ArrayList<Object[]>> service=new ExecutorCompletionService<>(threadPool);

        for(int i=0;i<originalFileList.size();i++)
            service.submit(new OneFile(originalFileList.get(i) , changedFileList.get(i)));

        for(int i=0;i<originalFileList.size();i++)
            result.addAll(service.take().get());

        threadPool.shutdown();

    }

    public ArrayList<Object[]> getResult() throws Exception{

        while(!threadPool.isTerminated()){
            Thread.sleep(1000);
        }

        return result;
    }

    private void traverseFold(File directory , ArrayList<File> fileList){
        for(File file : directory.listFiles()){
            if(file.isDirectory())
                traverseFold(file , fileList);
            else{
                String s=file.getAbsolutePath();
                if(s.length()>=4&&s.substring(s.lastIndexOf("."),s.length()).equals(".java"))
                    fileList.add(file);
            }
        }
    }
}
