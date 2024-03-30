package cz.stones.stone.stones.service;

import java.util.List;

public interface CsvService {

    void saveDataToCsv(List<String> lines, String filePath);
    
}