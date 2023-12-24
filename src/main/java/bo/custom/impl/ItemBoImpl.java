package bo.custom.impl;

import bo.custom.ItemBo;
import dao.custom.ItemDao;
import dao.custom.OrderDao;
import dao.custom.impl.ItemDaoImpl;
import dao.custom.impl.OrderDaoImpl;
import dto.ItemDto;

import java.sql.SQLException;
import java.util.List;

public class ItemBoImpl implements ItemBo {
    private ItemDao itemDao = new ItemDaoImpl();

    @Override
    public boolean saveItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean updateItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<ItemDto> allItems() throws SQLException, ClassNotFoundException {
        return null;
    }
}
