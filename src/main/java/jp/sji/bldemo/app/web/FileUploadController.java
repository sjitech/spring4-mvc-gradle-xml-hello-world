package jp.sji.bldemo.app.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.sji.bldemo.app.service.FileUploadService;
import jp.sji.bldemo.core.config.CoreConfig;

@Controller
public class FileUploadController {

    private final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    protected CoreConfig coreConfig;

    @Autowired
    protected FileUploadService fileUploadService;

    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public String provideUploadInfo() {
        log.info("show multiple file upload page");

        return "upload";
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public String handleFileUpload(
            @RequestParam("comment") String comment,
            @RequestParam("uploadFile") MultipartFile uploadFile,
            Model model){
        log.info("process uploaded files");

        log.debug("uploaded : " + comment);

        model.addAttribute("comment", comment);

        if (!uploadFile.isEmpty()) {
            String filename = uploadFile.getOriginalFilename();

            log.debug("origin file name : " + filename);

            String savePath = coreConfig.getUploadSavePath() + "/" + filename;

            log.debug("save file to " + savePath);

            try {
                byte[] bytes = uploadFile.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(savePath)));
                stream.write(bytes);
                stream.close();

                fileUploadService.saveUploadedFileToCloud(savePath);

                model.addAttribute("title", "Uploaded file successfully");
                model.addAttribute("message", "You successfully uploaded " + filename + "!");
                model.addAttribute("cssColor", "success");
            } catch (Exception e) {
                model.addAttribute("title", "Upload file failed");
                model.addAttribute("message", "You failed to upload " + filename + " => " + e.getMessage());
                model.addAttribute("cssColor", "danger");
            }
        } else {
            model.addAttribute("title", "No file selected");
            model.addAttribute("message", "No file was selected to upload.");
            model.addAttribute("cssColor", "info");
        }

        return "result";
    }

}
