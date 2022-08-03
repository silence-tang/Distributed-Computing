// MyRMIClient
package Client;
import InterFace.*;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Scanner;

// �ͻ����࣬����Remote����ʵ�ֺ��鼮ϵͳ�Ľ�����ע��ֻ��ͨ������Զ�̷����õ��䷵�صĽ������ֱ����ʵ������ʹ��print������ӡ����������
public class RMIClient {
	
    public static void ShowFunc(){
        System.out.println("------------��ӭʹ��XDUCS�鼮����ϵͳ------------\n�����б����£�");
        System.out.println("[1]: ����鼮����");
        System.out.println("[2]: ��ѯָ��ID�ŵ��鼮����");
        System.out.println("[3]: ��������ѯ�����������鼮�����б�֧��ģ��ƥ�䣩");
        System.out.println("[4]: ɾ��ָ��ID�ŵ��鼮����");
        System.out.println("[5]: �޸�ָ��ID�ŵ��鼮����");
        System.out.println("[6]: ��ӡ�鼮�б�");
        System.out.println("[7]: �˳�ϵͳ");
    }
    
    // main�������������ѡ����Ӧ�Ĳ�����Զ�̵��÷��������ṩ�ķ���
    public static void main(String args[]) {
        try {
            String RMI_NAME = "rmi://127.0.0.1:9527/BookInfoManageSystem";
            BookSystemInt MyInt = (BookSystemInt) Naming.lookup(RMI_NAME);
            System.out.println("��ѯע�����ĳɹ�!");
            // Ӧʹ��try�����input���󣬷�������leak����
            try (Scanner input = new Scanner(System.in)) {
				int pick = 7;
				ShowFunc();
				while ((pick = input.nextInt()) !=7){
				    switch (pick){
				        case 1:{
				        	while(true) {
				        		System.out.println("������鼮���������鼮ID������:");
					            String bookID = input.next();
					            String bookName = input.next();
					            Book book = new Book(bookID, bookName);
					            boolean flag = MyInt.add(book);
					            if(flag == true){
					                System.out.println("��ӳɹ����Ƿ������ӣ�(y/n)");
					                String choice = input.next();
					                if(choice.equals("y")) {
					                	continue;
					                }
					                else {
					                	break;
					                }
					            }
					            else{
					                System.out.println("���ʧ�ܣ��Ѵ���ͬID�鼮���Ƿ������ӣ�(y/n)");
					                String choice = input.next();
					                if(choice.equals("y")) {
					                	continue;
					                }
					                else {
					                	break;
					                }
					            }
				        	}
				        }
				        break;
				        
				        case 2:{
				            System.out.println("�������鼮���������鼮ID:");
				            String bookID = input.next();
				            Book book = MyInt.queryByID(bookID);
				            if(book == null){
				                System.out.println("δ�ҵ�IDΪ" + String.valueOf(bookID) + "���鼮");
				            }
				            else{
				                System.out.println("���ҳɹ�! �鼮��Ϣ����:");
				                System.out.println(book.BookInfo());
				            }
				        }
				        break;
				        
				        case 3:{
				            System.out.println("�������鼮�������������������ؼ���:");
				            String bookName = input.next();
				            ArrayList<Book> resultList = MyInt.queryByName(bookName);
				            if(resultList.size() == 0){
				                System.out.println("δ�ҵ�����Ϊ" + bookName + "����������" + bookName+ "���鼮");
				            }
				            else {
				                System.out.println("���ҳɹ�! �鼮��Ϣ����:");
				                for (Book b : resultList) {
				                    System.out.println(b.BookInfo());
				                }
				            }
				        }
				        break;
				        
				        case 4:{
				        	while(true) {
					            System.out.println("��ɾ���鼮���������鼮ID:");
					            String bookID = input.next();
					            boolean flag = MyInt.delete(bookID);
					            if(flag == true){
					                System.out.println("ɾ���ɹ����Ƿ����ɾ����(y/n)");
					                String choice = input.next();
					                if(choice.equals("y")) {
					                	continue;
					                }
					                else {
					                	break;
					                }
					            }
					            else{
					                System.out.println("ɾ��ʧ�ܣ�������IDΪ" + String.valueOf(bookID) + "���鼮���Ƿ����ɾ����(y/n)");
					                String choice = input.next();
					                if(choice.equals("y")) {
					                	continue;
					                }
					                else {
					                	break;
					                }
					            }
					        }
				        }
				        break;
				        
				        case 5:{
				        	while(true) {
					            System.out.println("���޸��������������鼮ID:");
					            String bookID = input.next();
					            System.out.println("���޸��������������µ�����:");
					            String newName = input.next();
					            boolean flag = MyInt.alter(bookID, newName);
					            if(flag == true){
					                System.out.println("�޸ĳɹ����Ƿ�����޸ģ�(y/n)");
					                String choice = input.next();
					                if(choice.equals("y")) {
					                	continue;
					                }
					                else {
					                	break;
					                }
					            }
					            else{
					                System.out.println("�޸�ʧ�ܣ�������IDΪ" + String.valueOf(bookID) + "���鼮���Ƿ�����޸ģ�(y/n)");
					                String choice = input.next();
					                if(choice.equals("y")) {
					                	continue;
					                }
					                else {
					                	break;
					                }
					            }
					        }
				        }
				        break;
				        
				        case 6:{
				            	System.out.println("�鼮�б�����:");
				            	System.out.println(MyInt.printBookList());
				        }
				        break;
				    }
				    System.out.println("\n�����빦�����[1]��[7]:");
				}
				// ������7�����˳�
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}