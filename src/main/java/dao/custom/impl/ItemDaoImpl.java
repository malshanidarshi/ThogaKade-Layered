package dao.custom.impl;

import db.DBConnection;
import dto.CustomerDto;
import dto.ItemDto;
import dao.custom.ItemDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDaoImpl implements ItemDao {

    @Override
    public boolean saveItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO item VALUES(?,?,?,?)";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean updateItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE item SET code=?, desc=?, unitPrice=? WHERE code=?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,dto.getCode());
        pstm.setString(2,dto.getDesc());
        pstm.setDouble(3,dto.getUnitPrice());
        pstm.setInt(4,dto.getQty());

        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean deleteItem(ItemDto id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<ItemDto> allItems() throws SQLException, ClassNotFoundException {
        List<ItemDto> list = new ArrayList<>();
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement("SELECT * FROM item");
        ResultSet resultSet = pstm.executeQuery();
        while (resultSet.next()){
            list.add(new ItemDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getDouble(3),
                    resultSet.getInt(4)
            ));
        }
        return list;
    }

    @Override
    public CustomerDto searchItem(String id) {
        return null;
    }
}
