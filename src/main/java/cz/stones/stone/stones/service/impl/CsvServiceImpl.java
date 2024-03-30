package cz.stones.stone.stones.service.impl;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.helger.commons.csv.CSVWriter;
import cz.stones.stone.stones.service.CsvService;
import cz.stones.stone.stones.service.StoneService;
import cz.stones.stone.stones.service.exception.StoneException;
import cz.stones.stone.stones.service.pojo.DimensionPojo;
import cz.stones.stone.stones.service.pojo.StonePojo;
import jakarta.annotation.PostConstruct;

@Service
public class CsvServiceImpl implements CsvService {

    @Autowired
    private StoneService stoneService;

    public void saveDataToCsv(List<String> lines, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toString()))) {
            new FileOutputStream(filePath).close();

            for (String line : lines) {
                writer.writeNext(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new StoneException("Error when write data into file %s ", filePath);
        }
    }

    //@PostConstruct
    public void importData() {
        try {
           List<StonePojo> pojos= readCSV("./data.csv");

           pojos.forEach(pojo -> stoneService.createStone(pojo));

        } catch (Exception e) {

           throw new RuntimeException(e);
        }

    }

    private List<StonePojo> readCSV(String fileName) throws IOException {
        List<StonePojo> data = new ArrayList<>();
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ";";

        try {
            br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null) {
                String[] row = line.split(csvSplitBy);
                
                data.add(procesLine(row));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return data;
    }

    private StonePojo procesLine(String[] line) {
        try {
            StonePojo pojo = new StonePojo();

            pojo.setManufacture(line[0]);
            pojo.setColor(line[1]);
            BigDecimal t = new BigDecimal(line[2].replaceAll("cm", "").trim());
            pojo.setThicknes(t);
            pojo.setDimensions(List.of(line[3].split("x")).stream()
                    .map(d -> new DimensionPojo(new BigDecimal(d))).toList());
            pojo.setRack(line[4]);
    
            if (line.length >5) {
                pojo.setNotes(line[6]);
            }
    
            pojo.setFlatDimensions(line[3]);
          
    
            return pojo;
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s", line));
        }
       
    }

}
