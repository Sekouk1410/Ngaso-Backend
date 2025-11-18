package com.ngaso.Ngaso.dto;

import java.util.List;

public class PagedUtilisateurResponse {
    private List<UtilisateurSummaryResponse> items;
    private int page;
    private int size;
    private long total;
    private int totalPages;
    private boolean hasNext;

    public PagedUtilisateurResponse() {}

    public PagedUtilisateurResponse(List<UtilisateurSummaryResponse> items, int page, int size, long total, int totalPages, boolean hasNext) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
    }

    public List<UtilisateurSummaryResponse> getItems() { return items; }
    public void setItems(List<UtilisateurSummaryResponse> items) { this.items = items; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }
}
