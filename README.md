# DiffFeatures
&emsp; A tool for describing diff in Java methods

## 1. Introduction
&emsp; This tool is designed for describing diff in Java methods.  

#### 1.1 What is diff?
&emsp; If you change a file, the difference between the changed file and the original file is called diff. Maybe the diff is little, for example, you change '++' to '--', or change 'int a=1;' to 'int a=0;'  

#### 1.2 What can this tool do
&emsp; This tool designed for describing diff using some metrics. Now we can only describing diff in Java method. By giving us a original method and a changed method, we can give a series of values to describe the diff based on our metrics.  

## 2. Usage of the tool
&emsp; We will introduce the usage of our tools in this chapter.

#### 2.1 Quick Start
&emsp; You can use it in Java. For example, you can input two Java code and get the values. Just like the code below shows.
```
DiffFeatures diffFeatures = new DiffFeatures();
String code1 = "class A{ void f(){ int a=1; } }";
String code2 = "class A{ void f(){ int a=2; } }";
try {
    diffFeatures.setInput(code1, code2);
    diffFeatures.build();
}catch (Exception e){
    e.printStackTrace();
}
```        
&emsp; You can get the result by calling .getResult(). We return the result in an arraylist. Each object[] in the list represents a changed method. The object[0] and object[1] record the path of the two files, and object[2] record the 95 values in the type of double[]. So you can get the value like this.
```
ArrayList<Object[]> result = diffFeatures.getResult();
double[] values = (double[]) result.get(0)[2];
```

#### 2.2 Multiple Input
&emsp; Besides inputing two string as Java code, you can input two Java file or two file directory. Just like the code shows.
```
diffFeatures.setInput(new File("D:\\origin"), new File("D:\\change"));
```
or
```
diffFeatures.setInput(new File("D:\\origin.java"), new File("D:\\change.java"));
```
&emsp; One thing needs to be notice that, if you input two file directory, we would treat the files that in the same level and have same name as one group and get the values between the files. If you want to compare two files in the different level or different filename, you can input the two file inside of two directories.

#### 2.3 Options
&emsp; Now we has only one option that define how many threads you are going to use ,and the default is 1. If you has a better CPU, you can increase the thread's number by calling setParallel(int threadNumber);

#### 2.4 Advanced usage
&emsp; We will put forward more features and more options soon.
