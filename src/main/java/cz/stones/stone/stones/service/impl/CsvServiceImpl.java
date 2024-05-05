package cz.stones.stone.stones.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.helger.commons.csv.CSVWriter;
import cz.stones.stone.stones.model.StateOfStone;
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
    private LocalDateTime localDateTime;


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


    @Override
    public String getPath() {
        return java.nio.file.Paths.get(System.getProperty("user.dir"), "src", "main", "resources",
                "static", "data.csv").toString();
    }

    @PostConstruct
    public void importData() {
        if (existFile()) {
            try {
                List<StonePojo> pojos = readCSV(getPath());

                pojos.forEach(pojo -> {
                    try {
                        stoneService.createStone(pojo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(pojo.getId().toString());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
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
                System.out.println(row[0]);
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
            pojo.setId(Long.valueOf(line[0].replaceAll("\"", "")));
            pojo.setManufacture(line[1]);
            pojo.setColor(line[2]);
            pojo.setNotes(line[3]);
            pojo.setRack(line[4]);
            pojo.setStateOfStone(StateOfStone.valueOf(line[5]));

            Instant instant = Instant.ofEpochMilli(Long.valueOf(line[6]));
            localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            pojo.setDateOfCreation(localDateTime);

            BigDecimal t = new BigDecimal(line[7].replaceAll("cm", "").trim().replaceAll("\"", ""));
            pojo.setThicknes(t);

            pojo.setDimensions(List.of(line[8].split("x")).stream()
                    .map(d -> new DimensionPojo(new BigDecimal(d.replaceAll("\"", "")))).toList());

            pojo.setFlatDimensions(line[8].replaceAll("\"", ""));

            return pojo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("%s", List.of(line).toString()));
        }

    }

    private Boolean existFile() {
        return new File(getPath()).exists();
    }
}
