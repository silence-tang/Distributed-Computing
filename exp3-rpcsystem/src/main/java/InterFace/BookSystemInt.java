// BookSystemInfo
package InterFace;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

// 接口定义类，定义所有Remote方法
// 接口类必须继承Remote类
// 所有方法需要声明java.rmi.RemoteException异常
public interface BookSystemInt extends Remote{
    boolean add(Book b) throws RemoteException;    // 增添一个书籍对象，返回添加成功/失败标志
    Book queryByID(String bookID) throws RemoteException;    // 查询指定ID的书籍对象，返回该书籍对象
    ArrayList<Book> queryByName(String name) throws RemoteException;    // 按书名查询符合条件的书籍对象，返回书籍对象列表
    boolean delete(String BookID) throws RemoteException;    // 删除指定ID的书籍对象，返回删除成功/失败标志
    boolean alter(String BookID, String newName) throws RemoteException;    // 删除指定ID的书籍对象，返回删除成功/失败标志
    StringBuilder printBookList() throws RemoteException;    // 打印所有书籍对象列表，在服务器端执行
}