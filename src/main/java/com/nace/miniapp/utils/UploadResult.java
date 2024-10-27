package com.nace.miniapp.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class UploadResult {
    private int uploadedNb;
    private int duplicatedNb;
}
