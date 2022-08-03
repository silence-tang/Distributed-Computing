// BookSystem
package InterFace;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

// Remote对象实现类，必须继承UnicastRemoteObject类，必须覆写接口中的全部抽象方法
// 在其中定义书籍系统相关的数据结构和操纵方法
public class BookSystemImpl extends UnicastRemoteObject implements BookSystemInt {
	public BookSystemImpl() throws RemoteException {    // 必须定义构造方法处理RemoteException
		super();
	}
	private static final long serialVersionUID = 1L;    // 必须声明static final serialVersionUID field of type long  
	private ArrayList<Book> bookList = new ArrayList<Book>();    // 初始化书籍列表为空，这里一定要用new ArrayList<Book>()，否则会报错空指针
	
    public boolean add(Book book) throws RemoteException{    // 增添一个书籍对象，返回添加成功/失败标志
    	for(Book b :bookList){    // 检查是否有同ID书籍，若是则返回false
            if(b.bookID.equals(book.bookID)){
                return false;
            }
        }
    	bookList.add(book);
    	return true;
    }

    public Book queryByID(String BookID) throws RemoteException{    // 查询指定ID的书籍对象，返回该书籍对象
        for(Book b :bookList){
            if(b.bookID.equals(BookID)){
                return b;
            }
        }
        return null;
    }
    
    public ArrayList<Book> queryByName(String name) throws RemoteException{    // 按书名查询符合条件的书籍对象，返回书籍对象列表
        ArrayList<Book> resultList = new ArrayList<Book>();
        for(Book b :bookList){
            if(b.bookName.indexOf(name, 0) != -1){
            	resultList.add(b);
            }
        }
        return resultList;
    }
    
    public boolean delete(String BookID) throws RemoteException{    // 删除指定ID的书籍对象，返回删除成功/失败标志
        for(Book b :bookList){
            if(b.bookID.equals(BookID)){
                bookList.remove(b);
                return true;
            }
        }
        return false;
    }
    
    public boolean alter(String BookID, String newName) throws RemoteException{    // 修改指定ID的书籍对象名称，返回修改成功/失败标志
        for(Book b :bookList){
            if(b.bookID.equals(BookID)){
                b.bookName = newName;
                return true;
            }
        }
        return false;
    }
    
    public StringBuilder printBookList() throws RemoteException{    // 打印所有书籍对象列表，若暂无对象则打印none.
    	StringBuilder str = new StringBuilder();
    	StringBuilder none = new StringBuilder();
    	if(bookList.size() == 0) {
    		none.append("none.");
    		return none;
    	}
    	else {
            for(Book b :bookList){
            	str.append(b.BookInfo());
            	str.append('\n');
            }
            return str;
    	}       
    }
}