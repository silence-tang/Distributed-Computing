// MyRMIClient
package Client;
import InterFace.*;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Scanner;

// 客户端类，调用Remote对象实现和书籍系统的交互，注意只能通过调用远程方法得到其返回的结果。若直接在实现类中使用print，则会打印到服务器端
public class RMIClient {
	
    public static void ShowFunc(){
        System.out.println("------------欢迎使用XDUCS书籍管理系统------------\n功能列表如下：");
        System.out.println("[1]: 添加书籍对象");
        System.out.println("[2]: 查询指定ID号的书籍对象");
        System.out.println("[3]: 按书名查询符合条件的书籍对象列表（支持模糊匹配）");
        System.out.println("[4]: 删除指定ID号的书籍对象");
        System.out.println("[5]: 修改指定ID号的书籍名称");
        System.out.println("[6]: 打印书籍列表");
        System.out.println("[7]: 退出系统");
    }
    
    // main方法：根据序号选择相应的操作，远程调用服务器端提供的方法
    public static void main(String args[]) {
        try {
            String RMI_NAME = "rmi://127.0.0.1:9527/BookInfoManageSystem";
            BookSystemInt MyInt = (BookSystemInt) Naming.lookup(RMI_NAME);
            System.out.println("查询注册中心成功!");
            // 应使用try块包裹input对象，否则会出现leak警告
            try (Scanner input = new Scanner(System.in)) {
				int pick = 7;
				ShowFunc();
				while ((pick = input.nextInt()) !=7){
				    switch (pick){
				        case 1:{
				        	while(true) {
				        		System.out.println("（添加书籍）请输入书籍ID和书名:");
					            String bookID = input.next();
					            String bookName = input.next();
					            Book book = new Book(bookID, bookName);
					            boolean flag = MyInt.add(book);
					            if(flag == true){
					                System.out.println("添加成功！是否继续添加？(y/n)");
					                String choice = input.next();
					                if(choice.equals("y")) {
					                	continue;
					                }
					                else {
					                	break;
					                }
					            }
					            else{
					                System.out.println("添加失败，已存在同ID书籍，是否继续添加？(y/n)");
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
				            System.out.println("（查找书籍）请输入书籍ID:");
				            String bookID = input.next();
				            Book book = MyInt.queryByID(bookID);
				            if(book == null){
				                System.out.println("未找到ID为" + String.valueOf(bookID) + "的书籍");
				            }
				            else{
				                System.out.println("查找成功! 书籍信息如下:");
				                System.out.println(book.BookInfo());
				            }
				        }
				        break;
				        
				        case 3:{
				            System.out.println("（查找书籍）请输入书名或书名关键字:");
				            String bookName = input.next();
				            ArrayList<Book> resultList = MyInt.queryByName(bookName);
				            if(resultList.size() == 0){
				                System.out.println("未找到书名为" + bookName + "或书名包含" + bookName+ "的书籍");
				            }
				            else {
				                System.out.println("查找成功! 书籍信息如下:");
				                for (Book b : resultList) {
				                    System.out.println(b.BookInfo());
				                }
				            }
				        }
				        break;
				        
				        case 4:{
				        	while(true) {
					            System.out.println("（删除书籍）请输入书籍ID:");
					            String bookID = input.next();
					            boolean flag = MyInt.delete(bookID);
					            if(flag == true){
					                System.out.println("删除成功！是否继续删除？(y/n)");
					                String choice = input.next();
					                if(choice.equals("y")) {
					                	continue;
					                }
					                else {
					                	break;
					                }
					            }
					            else{
					                System.out.println("删除失败，不存在ID为" + String.valueOf(bookID) + "的书籍，是否继续删除？(y/n)");
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
					            System.out.println("（修改书名）请输入书籍ID:");
					            String bookID = input.next();
					            System.out.println("（修改书名）请输入新的书名:");
					            String newName = input.next();
					            boolean flag = MyInt.alter(bookID, newName);
					            if(flag == true){
					                System.out.println("修改成功！是否继续修改？(y/n)");
					                String choice = input.next();
					                if(choice.equals("y")) {
					                	continue;
					                }
					                else {
					                	break;
					                }
					            }
					            else{
					                System.out.println("修改失败，不存在ID为" + String.valueOf(bookID) + "的书籍，是否继续修改？(y/n)");
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
				            	System.out.println("书籍列表如下:");
				            	System.out.println(MyInt.printBookList());
				        }
				        break;
				    }
				    System.out.println("\n请输入功能序号[1]—[7]:");
				}
				// 若输入7，则退出
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}