# DiffFeatures
&emsp; A tool for describing diff in Java methods

## 1. Introduction
&emsp; This tool is designed for describing diff in Java methods.  

#### 1.1 What is diff?
&emsp; If you change a file, the difference between the changed file and the original file is called diff. Maybe the diff is little, for example, you change '++' to '--', or change 'int a=1;' to 'int a=0;'  

#### 1.2 What can this tool do
&emsp; This tool designed for describing diff using some metrics. Now we can only describing diff in Java method. By giving us a original method and a changed method, we can give a series of values to describe the diff based on our metrics.  


## 2. Metrics
&emsp; We put forward 95 metrics to describe diff. The number represents the order of each value in the output.
![avatar](/features.png)


## 3. Usage of the tool
&emsp; We will introduce the usage of our tools in this chapter.

#### 3.1 Quick Start
&emsp; You can use it in Java. For example, you can input two Java code and get the values.
```
DiffFeatures diffFeatures = new DiffFeatures();
String code1 = "class A{ void f(){ int a=1; } }";
String code2 = "class A{ void f(){ int a=2; } }";
try {
    diffFeatures.setInput(code1, code2);
    diffFeatures.build();
    ArrayList<Object[]> result=diffFeatures.getResult();
    double[] values=(double[])result.get(0)[2];
}catch (Exception e){
    e.printStackTrace();
}
```        

#### 3.2 Multiple Input

#### 3.3 Options

#### 3.4 Advanced usage
