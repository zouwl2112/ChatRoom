package main;

/**
 * 学生代理类，也实现了Teacher接口，保存一个学生实体，这样既可以代理学生产生行为*/
public class StudentsProxy implements Teacher {
    //被代理的学生
    Student stu;

    public StudentsProxy(Teacher stu) {
        // 只代理学生对象
        if(stu.getClass() == Student.class) this.stu = (Student) stu;
    }

    //代理上交班费，调用被代理 学生的上交班费行为
    public void giveMoney() {
        System.out.println("张三家里是土豪，应该比其它人交更多的班费！");
        stu.giveMoney();
        System.out.println("张三班费交的最多，你就是班长了！");

    }
}
