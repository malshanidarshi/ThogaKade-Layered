package controller;

import bo.custom.CustomerBo;
import bo.custom.ItemBo;
import bo.custom.OrderBo;
import bo.custom.impl.CustomerBoImpl;
import bo.custom.impl.ItemBoImpl;
import bo.custom.impl.OrderBoImpl;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailDto;
import dto.OrderDto;
import dto.tm.OrderTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;
import dao.custom.CustomerDao;
import dao.custom.ItemDao;
import dao.custom.OrderDao;
import dao.custom.impl.CustomerDaoImpl;
import dao.custom.impl.ItemDaoImpl;
import dao.custom.impl.OrderDaoImpl;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PlaceOrderFormController implements Initializable {
    public Label lblOrderId;

    @FXML
    private JFXComboBox<?> cmbCustId;

    @FXML
    private JFXTextField txtCustName;

    @FXML
    private JFXComboBox<?> cmbCode;

    @FXML
    private JFXTextField txtDesc;

    @FXML
    private JFXTextField txtUnitPrice;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private JFXTreeTableView<OrderTm> tblItem;

    @FXML
    private TreeTableColumn<?, ?> colCode;

    @FXML
    private TreeTableColumn<?, ?> colDesc;

    @FXML
    private TreeTableColumn<?, ?> colQty;

    @FXML
    private TreeTableColumn<?, ?> colAmount;

    @FXML
    private TreeTableColumn<?, ?> colOption;

    @FXML
    private Label lblTotal;

    private double total=0;

    private CustomerBo customerBo = new CustomerBoImpl();
    private OrderBo orderBo = new OrderBoImpl();
    //    private ItemDao itemDao = new ItemDaoImpl();
    private ItemBo itemBo = new ItemBoImpl();
    private List<CustomerDto> customers;
    private List<ItemDto> items;

    private ObservableList<OrderTm> tmList = FXCollections.observableArrayList();

//    private OrderDao orderDao = new OrderDaoImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDesc.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        try {
            customers = customerBo.allCustomers();
            items = itemBo.allItems();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        loadCustomerIds();
        loadItemCodes();

        cmbCustId.getSelectionModel().selectedItemProperty().addListener((observableValue, o, newValue) -> {
            for (CustomerDto dto:customers) {
                if (dto.getId().equals(newValue.toString())){
                    txtCustName.setText(dto.getName());
                }
            }
        });

        cmbCode.getSelectionModel().selectedItemProperty().addListener((observableValue, o, newValue) -> {
            for (ItemDto dto:items) {
                if (dto.getCode().equals(newValue.toString())){
                    txtDesc.setText(dto.getDesc());
                    txtUnitPrice.setText(String.format("%.2f",dto.getUnitPrice()));
                }
            }
        });
        //setOrderId();
    }

    private void loadCustomerIds() {
        ObservableList list = FXCollections.observableArrayList();

        for (CustomerDto dto:customers) {
            list.add(dto.getId());
        }

        cmbCustId.setItems(list);
    }

    private void loadItemCodes() {
        ObservableList list = FXCollections.observableArrayList();

        for (ItemDto dto:items) {
            list.add(dto.getCode());
        }

        cmbCode.setItems(list);
    }

    private void setOrderId() {
        try {
            String id = orderBo.getLastOrder().getOrderId();
            if (id!=null){
                int num = Integer.parseInt(id.split("[D]")[1]);
                num++;
                lblOrderId.setText(String.format("D%03d",num));
            }else{
                lblOrderId.setText("D001");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToCartButtonOnAction(ActionEvent event) {
        JFXButton btn = new JFXButton("Delete");

        OrderTm tm = new OrderTm(
                cmbCode.getValue().toString(),
                txtDesc.getText(),
                Integer.parseInt(txtQty.getText()),
                Double.parseDouble(txtUnitPrice.getText())*Integer.parseInt(txtQty.getText()),
                btn
        );
        btn.setOnAction(actionEvent -> {
            tmList.remove(tm);
            total-=tm.getAmount();
            lblTotal.setText(String.format("%.2f",total));
            tblItem.refresh();
        });
        boolean isExist = false;
        for (OrderTm order:tmList) {
            if (order.getCode().equals(tm.getCode())){
                order.setQty(order.getQty()+tm.getQty());
                order.setAmount(order.getAmount()+tm.getAmount());
                isExist = true;
                total+= tm.getAmount();
            }
        }
        if (!isExist){
            tmList.add(tm);
            total+=tm.getAmount();
        }

        lblTotal.setText(String.format("%.2f",total));

        TreeItem treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
        tblItem.setRoot(treeItem);
        tblItem.setShowRoot(false);
    }

    public void placeOrderButtonOnAction(ActionEvent event) {
        List<OrderDetailDto> list = new ArrayList<>();
        for (OrderTm tm:tmList) {
            list.add(new OrderDetailDto(
                    lblOrderId.getText(),
                    tm.getCode(),
                    tm.getQty(),
                    tm.getAmount()/tm.getQty()
            ));
        }

        OrderDto dto = new OrderDto(
                lblOrderId.getText(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")),
                cmbCustId.getValue().toString(),
                list
        );


        try {
            boolean isSaved = orderBo.saveOrder(dto);
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION, "Order Saved!").show();
                setOrderId();
            }else{
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void backButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) tblItem.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
