package com.ngaso.Ngaso.dto;

import java.util.List;

public class PagedProfessionnelResponse {
    private List<ProfessionnelSummaryResponse> items;
    private int page;
    private int size;
    private long total;
    private int totalPages;
    private boolean hasNext;

    public PagedProfessionnelResponse() {}

    public PagedProfessionnelResponse(List<ProfessionnelSummaryResponse> items, int page, int size, long total, int totalPages, boolean hasNext) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
    }

    public List<ProfessionnelSummaryResponse> getItems() { return items; }
    public void setItems(List<ProfessionnelSummaryResponse> items) { this.items = items; }

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
