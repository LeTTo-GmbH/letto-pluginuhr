package at.letto.tools;

import at.letto.globalinterfaces.ImageService;
import at.letto.tools.dto.FileBase64Dto;

public class LoadImgSrcTest {

    public static void main(String[] args) {
        FileBase64Dto fileBase64Dto = ImageService.loadImageSource("https://media.gettyimages.com/id/1355036780/de/foto/mount-everest-nordseite-des-mount-qomolangma-blick-vom-chinesischen-mount-qomolangma-base-camp.jpg?s=2048x2048&w=gi&k=20&c=jMdp1hUedFmnRx_3jStMakt_SrGqpe0w4o9F2w0qEDk=");
        fileBase64Dto = ImageService.loadImageSource("https://www.gettyimages.at/detail/foto/climbing-everest-lizenzfreies-bild/172699850?adppopup=true");
    }
}
