//  Book
package InterFace;

import java.io.Serializable;

// 书籍类：定义书对象的属性及方法,必须定义为公共类
public class Book implements Serializable {
	private static final long serialVersionUID = 1L;
	// 书籍类属性
    public String bookID;
    public String bookName;
    // 构造方法
    public Book(String id, String name){
    	bookID = id;
    	bookName = name;
    }
    // BookInfo方法：返回书籍信息
    public String BookInfo(){
        return("书籍ID: " + bookID + " 书名: " + bookName);
    }
}