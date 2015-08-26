package jp.sji.bldemo.app.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {

    private final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public String provideUploadInfo() {
        logger.info("show multiple file upload page");

        return "upload";
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(
            @RequestParam("comment") String comment,
            @RequestParam("uploadFile") MultipartFile uploadFile,
            Model model){
        logger.info("process uploaded files");

        logger.debug("uploaded : " + comment);

        model.addAttribute("comment", comment);

        if (!uploadFile.isEmpty()) {
            try {
                byte[] bytes = uploadFile.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(comment)));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + comment + "!";
            } catch (Exception e) {
                return "You failed to upload " + comment + " => " + e.getMessage();
            }
        } else {
            model.addAttribute("title", comment);
            model.addAttribute("message", comment);
            model.addAttribute("cssColor", comment);
        }

        return "result";
    }

}
