package View;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public abstract class TableModel<T> extends AbstractTableModel {
    String[] headers;
    int[] indexes;
    Vector<T> data;
    int currentPage = 1;
    int pageSize = 5;

    public TableModel(String[] headers, int[] indexes) {
        this.headers = new String[headers.length];
        System.arraycopy(headers, 0, this.headers, 0, headers.length);
        this.indexes = new int[indexes.length];
        System.arraycopy(indexes, 0, this.indexes, 0, indexes.length);
        this.data = new Vector<>();
    }

    public Vector<T> getData() {
        return data;
    }

    public void setData(Vector<T> data) {
        this.currentPage = 1;
        this.data = data;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        if (currentPage > this.getPageCount()) this.currentPage = 1;
        else if (currentPage < 1) this.currentPage = this.getPageCount();
        else this.currentPage = currentPage;
    }

    public int getPageCount() {
        return (int) Math.ceil(data.size() / (1.0 * pageSize));
    }

    @Override
    public String getColumnName(int column) {
        return (column >= 0 && column < indexes.length) ? this.headers[column] : "";
    }

    @Override
    public int getRowCount() {
        int tmp = (currentPage - 1) * pageSize;
        return Math.min(data.size() - tmp, pageSize);
    }

    @Override
    public int getColumnCount() {
        return this.headers.length;
    }

    @Override
    public abstract Object getValueAt(int rowIndex, int columnIndex);
}
