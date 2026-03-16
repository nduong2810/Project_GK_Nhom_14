package vn.edu.ute.ui.certificate;

import vn.edu.ute.model.Certificate;
import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp `CertificateTableModel` là mô hình dữ liệu cho JTable hiển thị thông tin chứng chỉ.
 */
public class CertificateTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Tên Học Viên", "Lớp Học", "Tên Chứng Chỉ", "Ngày Cấp", "Số Serial"};
    private List<Certificate> data = new ArrayList<>();
    private List<Certificate> filteredData = new ArrayList<>();
    private String filterKeyword = "";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Cập nhật dữ liệu cho model.
     */
    public void setData(List<Certificate> data) {
        this.data = data;
        applyFilter();
    }

    /**
     * Thiết lập từ khóa lọc.
     */
    public void setFilter(String keyword) {
        this.filterKeyword = keyword != null ? keyword.trim().toLowerCase() : "";
        applyFilter();
    }

    /**
     * Áp dụng bộ lọc.
     */
    private void applyFilter() {
        if (filterKeyword.isEmpty()) {
            filteredData = new ArrayList<>(data);
        } else {
            filteredData = data.stream()
                    .filter(c -> {
                        String studentName = c.getStudent() != null ? c.getStudent().getFullName().toLowerCase() : "";
                        String certName = c.getCertName() != null ? c.getCertName().toLowerCase() : "";
                        String serial = c.getSerialNo() != null ? c.getSerialNo().toLowerCase() : "";
                        return studentName.contains(filterKeyword) || certName.contains(filterKeyword) || serial.contains(filterKeyword);
                    })
                    .collect(Collectors.toList());
        }
        fireTableDataChanged();
    }

    /**
     * Lấy chứng chỉ tại một hàng.
     */
    public Certificate getAt(int row) {
        if (row < 0 || row >= filteredData.size()) return null;
        return filteredData.get(row);
    }

    @Override public int getRowCount() { return filteredData.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Certificate c = filteredData.get(rowIndex);
        switch (columnIndex) {
            case 0: return c.getCertificateId();
            case 1: return c.getStudent() != null ? c.getStudent().getFullName() : "";
            case 2: return c.getClassEntity() != null ? c.getClassEntity().getClassName() : "(Không có lớp)";
            case 3: return c.getCertName();
            case 4: return c.getIssueDate() != null ? c.getIssueDate().format(formatter) : "";
            case 5: return c.getSerialNo();
            default: return "";
        }
    }
}
