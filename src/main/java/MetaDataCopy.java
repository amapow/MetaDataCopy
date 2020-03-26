import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


class Run {
    String path;
    public String print() throws ImageProcessingException, IOException {
        File file = new File(path);
        Metadata metadata = JpegMetadataReader.readMetadata(file);
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (directory == null) {
            return "null";
        }
        String date = directory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
        return date;
    }
    public String formatDate() throws ImageProcessingException, IOException {
        String date = print();
        if(date.length() < 10) {
            return "null";
        }
        String formatDate = date.substring(0,10);
        return formatDate;
    }
    public void makeDirectory(String path, String outputPath, String fileName) throws ImageProcessingException, IOException {
        this.path = path;
        File checkOutPath = new File(outputPath);
        if (checkOutPath.exists() == false) {
            checkOutPath.mkdir();
        }

        String formatDate = formatDate();
        if (formatDate() == "null")
            return;

        String year = formatDate.substring(0, 4);
        String month = formatDate.substring(5, 7);
        String day = formatDate.substring(8, 10);
        String outputPath_y = outputPath + year;
        File file_y = new File(outputPath_y);
        String outputPath_m = outputPath_y + "/" + month;
        File file_m = new File(outputPath_m);
        String outputPath_d = outputPath_m + "/" + day;
        File file_d = new File(outputPath_d);
        if (file_y.exists() == false) {
            file_y.mkdir();
        }
        if (file_m.exists() == false) {
            file_m.mkdir();
        }
        if (file_d.exists() == false) {
            file_d.mkdir();
        }

        File copyFile = new File(outputPath_d + "/" + fileName);
        copyFile(copyFile);
    }
    public void copyFile(File copyFile) throws IOException {
        if (copyFile.exists() == false){
            long fsize = 0;
            FileInputStream fis = new FileInputStream(path);
            FileOutputStream fos = new FileOutputStream(copyFile);
            FileChannel fcin = fis.getChannel();
            FileChannel fcout = fos.getChannel();

            fsize = fcin.size();
            fcin.transferTo(0, fsize, fcout);

            fcout.close();
            fcin.close();
            fos.close();
            fis.close();
        }
    }
}
class CheckFile {
    public void checkFile(String inputPath, String outputPath) throws ImageProcessingException, IOException {
        if (inputPath.endsWith("/") == false) {
            inputPath = inputPath + "/";
        }
        if (outputPath.endsWith("/") == false) {
            outputPath = outputPath + "/";
        }

        File file = new File(inputPath);
        String[] flist = file.list();
        String path;

        for (int i = 0; i < flist.length; i++) {
            flist[i] = flist[i].toLowerCase();
            if (flist[i].length() > 4) {
                if (flist[i].indexOf(".") == -1) {
                    String tempPath = inputPath + flist[i];
                    flist[i] = tempPath + "/";
                    File file1 = new File(flist[i]);
                    String[] flist_dir = file1.list();
                    for (int j = 0 ; j < flist_dir.length ; j++){
                        if (flist_dir[j].substring(flist_dir[j].length() - 3).equals("JPG") || flist_dir[j].substring(flist_dir[j].length() - 4).equals("JPEG")) {
                            Run run = new Run();
                            File file2 = new File(file1 + "/" + flist_dir[j]);
                            path = file2.toString();
                            flist_dir[j] = flist_dir[j].toUpperCase();
                            run.makeDirectory(path, outputPath, flist_dir[j]);
                        }
                    }
                }
                if (flist[i].substring(flist[i].length() - 3).equals("jpg") || flist[i].substring(flist[i].length() - 4).equals("jpeg")) {
                    Run run = new Run();
                    File file2 = new File(inputPath + flist[i]);
                    path = file2.toString();
                    flist[i] = flist[i].toUpperCase();
                    run.makeDirectory(path, outputPath, flist[i]);
                }
            }
        }
    }
}
public class MetaDataCopy {
    public String inputPath() {
        JFileChooser input = new JFileChooser();
        input.setFileSelectionMode(input.DIRECTORIES_ONLY);
        int openDialog = input.showOpenDialog(null);
        String inputFilePath = input.getSelectedFile().getPath();

        return inputFilePath;
    }

    public String outputPath() {
        JFileChooser output = new JFileChooser();
        output.setFileSelectionMode(output.DIRECTORIES_ONLY);
        int openDialog = output.showOpenDialog(null);
        String outputFilePath = output.getSelectedFile().getPath();

        return outputFilePath;
    }

    public static void main(String[] args) throws ImageProcessingException, IOException {
        MetaDataCopy meta = new MetaDataCopy();
        CheckFile run = new CheckFile();
        run.checkFile(meta.inputPath(), meta.outputPath());
    }
}