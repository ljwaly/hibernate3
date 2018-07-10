package com.lamp.hibernate.test;

import org.hibernate.Session;
import org.junit.Test;

import com.lamp.hibernate.pojo.Course;
import com.lamp.hibernate.pojo.Customer;
import com.lamp.hibernate.pojo.Order;
import com.lamp.hibernate.pojo.Student;
import com.lamp.hibernate.util.HibernateUtils;

public class HibernateTest {

	@Test
	public void createTable() {
		HibernateUtils.getSessionFactory();
	}

	/*
	 * 双向关联保存
	 */
	@Test
	public void testSave() {
		Session session = HibernateUtils.openSession();
		session.beginTransaction();

		Customer c1 = new Customer();
		c1.setName("kobe");

		Order o1 = new Order();
		o1.setName(c1.getName() + "的订单2");

		// 双向建立关系
		c1.getOrders().add(o1);
		o1.setCustomer(c1);

		// 必须同时保存两个对象，否则会报错
		session.save(c1);
		// session.save(o1);

		session.getTransaction().commit();
		session.close();
	}

	@Test
	public void testSave2() {
		Session session = HibernateUtils.openSession();
		session.beginTransaction();

		Customer c1 = new Customer();
		c1.setName("lucy");

		Order o1 = new Order();
		o1.setName(c1.getName() + "的订单1");

		Order o2 = new Order();
		o2.setName(c1.getName() + "的订单2");

		Order o3 = new Order();
		o3.setName(c1.getName() + "的订单3");

		Order o4 = new Order();
		o4.setName(c1.getName() + "的订单4");

		// 当级联保存的时候，当我们在Customer.hbm.xml中设置了级联关系的时候，
		// 那么在设置关系的时候，只需要向Customer的orders集合中添加Order，就可以进行级联保存
		c1.getOrders().add(o1);
		c1.getOrders().add(o2);
		c1.getOrders().add(o3);
		c1.getOrders().add(o4);

		// o1.setCustomer(c1);

		session.save(c1);
		// session.save(o1);

		session.getTransaction().commit();
		session.close();
	}

	@Test
	public void testDelete() {
		Session session = HibernateUtils.openSession();
		session.beginTransaction();

		// 删除多方
		// Order o1 = new Order();
		// o1.setId(17);
		// session.delete(o1);

		// 删除一方:如果一方是一个脱管态对象，那么先将多方的外键置空（解除关系），然后在删除
		// Customer c1 = new Customer();
		// c1.setId(8);

		// 删除一方：一方是一个持久态对象,也是先解除关系，然后在删除
		// 问题：如果客户都不存在了，那么这个订单还有存在的意义吗？
		// 答：订单的存在已经没有意义了，所以说我们可以通过级联删除，
		// 在删除Customer的时候，自动删除与Customer相关的order
		Customer c1 = (Customer) session.get(Customer.class, 2);

		session.delete(c1);

		session.getTransaction().commit();
		session.close();
	}

	@Test
	public void testInverse() {
		Session session = HibernateUtils.openSession();
		session.beginTransaction();

		Customer customer = (Customer) session.get(Customer.class, 3);
		Order order = (Order) session.get(Order.class, 6);

		// 建立双向关联关系
		customer.getOrders().add(order);
		order.setCustomer(customer);
		// /保存客户
		session.save(customer);

		session.getTransaction().commit();
		session.close();
	}

	@Test
	public void testSave3() {
		Session session = HibernateUtils.openSession();
		session.beginTransaction();
		// 新建一个学生
		Student stu = new Student();
		stu.setName("rose");
		// 新建课程
		Course course = new Course();
		course.setName("hibernate");	
		// /双向关联
		stu.getCourses().add(course);
		course.getStudents().add(stu);

		// 发现抛出异常，如何解决
		// 1.外键维护权，一方放弃inverse="true"，并且不放弃维护权的一方，加入 cascade="save-update"
		// 2.建立关系是，只需要建立一方的关系即可，并且建立关系的一方，加入 cascade="save-update"

		// 双向保存
		session.save(course);
		// session.save(stu);

		session.getTransaction().commit();
		session.close();

	}

	@Test
	public void deleteRelation() {
		Session session = HibernateUtils.openSession();
		session.beginTransaction();

		Student student = (Student) session.get(Student.class, 2);
		Course course = (Course) session.get(Course.class, 2);

		// 注意：由于course放弃了对集合的维护权，所以此时只能在Student这一方进行集合操作
		student.getCourses().remove(course);

		// 不需要手动的删除，直接使用快照的更新功能，commit会隐式的flush
		session.getTransaction().commit();
		session.close();

	}

	@Test
	public void changeRelation() {
		Session session = HibernateUtils.openSession();
		session.beginTransaction();

		// 让2号学生选修3号课程
		Student student = (Student) session.get(Student.class, 2);
		Course course = (Course) session.get(Course.class, 2);
		// 解除关系
		student.getCourses().remove(course);

		// 查找3号课程
		Course course2 = (Course) session.get(Course.class, 3);
		// 添加关系
		student.getCourses().add(course2);

		// flush
		session.getTransaction().commit();
		session.close();
	}


	@Test
	public void testDeleteEntity() {
		Session session = HibernateUtils.openSession();
		session.beginTransaction();

		Student stu = (Student) session.get(Student.class, 3);
		// 删除
		session.delete(stu);

		// flush
		session.getTransaction().commit();
		session.close();

	}

}
