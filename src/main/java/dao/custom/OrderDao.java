package dao.custom;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import dao.CrudDao;
import dto.OrderDto;
import entity.Orders;

import java.sql.SQLException;

public interface OrderDao extends CrudDao<OrderDto> {
    //boolean saveOrder(Orders entity) throws SQLException, ClassNotFoundException;
    OrderDto getLastOrder() throws SQLException, ClassNotFoundException;
}
