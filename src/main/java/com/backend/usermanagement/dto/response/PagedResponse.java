package com.backend.usermanagement.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

// Generic paginated response wrapper
// T tipinde içerik + sayfalama metadata'sı döndürür
public class PagedResponse<T> {

    private List<T> content;        // Asıl veri listesi
    private int currentPage;        // Şu anki sayfa (0'dan başlar)
    private int totalPages;         // Toplam sayfa sayısı
    private long totalElements;     // Toplam kayıt sayısı
    private int pageSize;           // Sayfa başına kayıt sayısı
    private boolean first;          // İlk sayfa mı?
    private boolean last;           // Son sayfa mı?

    public PagedResponse(Page<T> page) {
        this.content = page.getContent();
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.pageSize = page.getSize();
        this.first = page.isFirst();
        this.last = page.isLast();
    }

    public List<T> getContent() { return content; }
    public int getCurrentPage() { return currentPage; }
    public int getTotalPages() { return totalPages; }
    public long getTotalElements() { return totalElements; }
    public int getPageSize() { return pageSize; }
    public boolean isFirst() { return first; }
    public boolean isLast() { return last; }
}
