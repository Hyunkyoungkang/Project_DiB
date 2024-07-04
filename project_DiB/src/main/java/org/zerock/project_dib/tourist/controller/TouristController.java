package org.zerock.project_dib.tourist.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.project_dib.tourist.dto.TouristDTO;
import org.zerock.project_dib.tourist.dto.TouristImgDTO;
import org.zerock.project_dib.tourist.dto.upload.UploadFileDTO;
import org.zerock.project_dib.tourist.service.TouristService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tourist")
public class TouristController {

    private final TouristService touristService;

    @GetMapping("/list")
    public String getList(Model model) {
        List<TouristDTO> list = touristService.getList();
        model.addAttribute("list", list);
        return "/tourist/list";
    }

    @GetMapping("/read/{tno}")
    public String read(@PathVariable("tno") int tno, Model model) {
        List<String> fileNames = touristService.getImgList(tno);
        TouristDTO dto = touristService.read(tno);
        dto.setFileNames(fileNames);
        model.addAttribute("dto", dto);
        return "/tourist/read";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/register")
    public void registerGET() {
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/register")
    public String register(TouristDTO touristDTO, @RequestParam("file") MultipartFile file) throws IOException {
        int tno = touristService.register(touristDTO);
        touristDTO.setTno(tno);
        if (!file.isEmpty()) {
            touristService.registerImg(touristDTO, file);
        }

        return "redirect:/tourist/list";
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/remove/{tno}")
    public String remove(@PathVariable("tno") int tno) {
        touristService.remove(tno);
        return "redirect:/tourist/list";
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/modify/{tno}")
    public String modifyGET(@PathVariable("tno") int tno, TouristDTO touristDTO, Model model) {
        touristDTO.setTno(tno);
        TouristDTO dto = touristService.read(tno);
        model.addAttribute("dto", dto);
        return "/tourist/modify";

    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/modify/{tno}")
    public String modify(@PathVariable("tno") int tno, TouristDTO touristDTO, @RequestParam("file") MultipartFile file)  throws IOException {
        touristDTO.setTno(tno);
        if(!file.isEmpty()) {
            touristService.removeImgs(tno);
            touristService.registerImg(touristDTO, file);
        }
        touristService.modify(touristDTO);
        return "redirect:/tourist/list";
    }

//    @GetMapping("/{tno}/images")
//    public List<TouristImgDTO> getImgList(@PathVariable("tno") int tno, Model model) {
//        List<TouristImgDTO> fileNames = touristService.getImgList(tno);
//        model.addAttribute("fileNames", fileNames);
//        return touristService.getImgList(tno);
//    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/registerImg/{tno}")
    @ResponseBody
    public void registerImg(@PathVariable("tno") int tno, @RequestParam MultipartFile file) throws IOException {
        TouristDTO touristDTO = new TouristDTO();
        touristDTO.setTno(tno);

        if (!file.isEmpty()) {
            var upDownDto = new UploadFileDTO();
            var imgList = new ArrayList<MultipartFile>();
            imgList.add(file);
            upDownDto.setFiles(imgList);

            var updownController = new UpDownController();
            updownController.upload(upDownDto);
        }

        touristService.registerImg(touristDTO, file);
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/modifyImg/{tno}", consumes = "multipart/form-data")
    public void modifyImg(@PathVariable("tno") int tno, TouristDTO touristDTO ,@RequestParam("file") MultipartFile file, Model model) {
        touristDTO.setTno(tno);
        TouristDTO dto = touristService.read(tno);
        model.addAttribute("dto", dto);
        model.addAttribute("file", file);

        if (!file.isEmpty()) {
            var upDownDto = new UploadFileDTO();
            var imgList = new ArrayList<MultipartFile>();
            imgList.add(file);
            upDownDto.setFiles(imgList);

            var updownController = new UpDownController();
            updownController.upload(upDownDto);
        }

    }




//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/remove/{tno}/images")
    @ResponseBody
    public void removeImgs(@PathVariable("tno") int tno) {
        touristService.removeImgs(tno);
    }


}