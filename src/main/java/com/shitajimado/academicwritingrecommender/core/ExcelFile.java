package com.shitajimado.academicwritingrecommender.core;

import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class ExcelFile {

    //создание файла Excel
    public static void create(Statistics input_data, List<String> documentsNames, String corporaName){
        Map<String, double[]> data = new HashMap<>();

        for(StatisticsNode sn: input_data.getAnnotations()){
            data.put(sn.getName(), sn.getCount());
        }
        String[] statisticsHeadings = ExcelFile.getStatisticsHeadings();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Статистика по маркерам");
 
        XSSFFont font= workbook.createFont();
        font.setBold(true);

        //настройка стилей
        CellStyle cs_bold = workbook.createCellStyle();;
        cs_bold.setFont(font);

        int rownum = 0;
        Cell cell;
        Row row;
        
        row = sheet.createRow(rownum);

        //заполняем заголовки - маркер+статистические показатели
        for(int i =0; i<statisticsHeadings.length; i++){
            cell = row.createCell(i, CellType.STRING);
            cell.setCellValue(statisticsHeadings[i]);
            cell.setCellStyle(cs_bold);
        }

        //запись данных в ячейки
        for(java.util.Map.Entry<String, double[]> mark : data.entrySet()) {
            double[] values = mark.getValue();

            //наименование маркера
            rownum++;
            row = sheet.createRow(rownum);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(mark.getKey());

            //заполнение значений статистик
            for(int i=0; i<values.length;i++){          
                if(values[i]==0.7777){
                    cell = row.createCell(i+1, CellType.STRING);       
                    cell.setCellValue("-");
                }
                else{
                    cell = row.createCell(i+1, CellType.NUMERIC);       
                    cell.setCellValue(values[i]);
                }
            }
        }

        XSSFSheet sheet2 = workbook.createSheet("Количество повторений маркеров в тексте");
        rownum = 0;
        int colnum = 0;
        row = sheet2.createRow(rownum);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Текст");
        cell.setCellStyle(cs_bold);

        //заполняем наименования маркеров - в строку
        for(java.util.Map.Entry<String, double[]> mark : data.entrySet()){
            colnum++;
            cell = row.createCell(colnum, CellType.STRING);
            cell.setCellValue(mark.getKey());
            cell.setCellStyle(cs_bold);
        }

        //запись данных в ячейки
        for(String docName: documentsNames){
            //1 столбец - текст в корпусе
            colnum=0;
            rownum++;
            row = sheet2.createRow(rownum);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(docName);
            cell.setCellStyle(cs_bold);

            for(StatisticsNode sn: input_data.getAnnotations()) {
                colnum++;
                cell = row.createCell(colnum, CellType.NUMERIC);
                cell.setCellValue(sn.getFrequency()[rownum-1]);
            }
        }

        //если проанализирован корпус текстов, то добавляется описание распределений по маркерам
        if(data.size()>1){

            //добавление второго листа
            XSSFSheet sheet3 = workbook.createSheet("Краткое описание распределения");
            int rownum2 = 0;
            Cell cell2;
            Row row2;

            //заполнение заголовков
            row2 = sheet3.createRow(0);
            cell2 = row2.createCell(0, CellType.STRING);
            cell2.setCellValue("Маркер");
            cell2.setCellStyle(cs_bold);

            cell2 = row2.createCell(1, CellType.STRING);
            cell2.setCellValue("Краткое описание");
            cell2.setCellStyle(cs_bold);

            //настройка стилей
            CellStyle cs = workbook.createCellStyle();;
            cs.setWrapText(true);

            String[] info_distr = DistributionDescription(data); //массив с описанием распределения по маркерам
            int j=0;
            //заполнение ячеек кратким описанием распределений
            for(java.util.Map.Entry<String, double[]> mark : data.entrySet()) {
                rownum2++;
                row2 = sheet3.createRow(rownum2);
                cell2 = row2.createCell(0, CellType.STRING);
                cell2.setCellValue(mark.getKey());

                cell2 = row2.createCell(1, CellType.STRING);
                cell2.setCellValue(info_distr[j]);
                cell2.setCellStyle(cs);
                j++;
            }
            
            for (int i = 0; i < sheet3.getRow(0).getPhysicalNumberOfCells(); i++) {
                sheet3.autoSizeColumn(i);
            }    
        }
        

        //автоширина столбцов
        for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
            sheet.autoSizeColumn(i);
        }      

        for (int i = 0; i < sheet2.getRow(0).getPhysicalNumberOfCells(); i++) {
            sheet2.autoSizeColumn(i);
        }   

        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy HH-mm-ss");
        String date = formatForDateNow.format(new Date());

        String home = System.getProperty("user.home");
        try (var outputStream = new FileOutputStream(home+"/Downloads/statistics_" + corporaName + "(" + date + ").xlsx")) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //формирование описания распределений
    static String[] DistributionDescription(Map<String, double[]> data){
        String[] Descriptions = new String[data.size()];
        int i=0;
        for(java.util.Map.Entry<String, double[]> mark : data.entrySet()) {
            double[] values = mark.getValue();
            String description = "";

            //если среднее выборочное равно медиане
            if(values[0]==values[2])
                description+="среднее выборочное равно медиане;";
            else if(values[0]>values[2])
                description+="среднее выборочное больше медианы;";
            else
                description+="среднее выборочное меньше медианы;";

            //коэффициент вариации
            if(values[5]<17)
                description+= "\n" +"абсолютно однородная совокупность данных;";
            else if(values[5]>=17 & values[5]<=33)
                description+= "\n" +"достаточно однородная совокупность данных;";
            else if(values[5]>33 & values[5]<=40)
                description+= "\n" +"недостаточно однородная совокупность данных;";
            else
                description+= "\n" +"большая колеблемость совокупности данных;";

            //коэффициент асимметрии
            if(values[6]>0)
                description+= "\n" + "правосторонняя асимметрия;";
            else if(values[6]<0)
                description+="\n" + "левосторонняя асимметрия;";
            else
                description+="\n" + "симметричность распределения данных;";

            //эксцесс
            if(values[7]>0)
                description+= "\n" +"островершинность распределения";
            else if(values[7]<0)
                description+= "\n" +"плосковершинность распределения";
            else
                description+= "\n" +"вершина как у нормального распределения";

            Descriptions[i] = description;
            i++;
        }
        return Descriptions;
    }

    private static String[] getStatisticsHeadings(){
        return new String[] { "Маркер", "Среднее выборочное", "Мода", "Медиана", "Дисперсия",
        "Среднеквадратичное отклонение", "Коэффициент вариации, %", "Коэффициент асимметрии", "Эксцесс",
        "Минимальное значение", "Квартиль 0,25", "Квартиль 0,5","Квартиль 0,75", "Максимальное значение",
        "Интерквартильный размах"};
    }
 
}
