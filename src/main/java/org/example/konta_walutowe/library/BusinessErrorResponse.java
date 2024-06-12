package org.example.konta_walutowe.library;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BusinessErrorResponse {
    private int status;
    private String message;
    private long timestamp;
}
