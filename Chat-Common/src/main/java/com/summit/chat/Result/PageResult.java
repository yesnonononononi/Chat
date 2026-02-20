package com.summit.chat.Result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class PageResult{
    private long total;
    private List records;

}

