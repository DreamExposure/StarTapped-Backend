package org.dreamexposure.tap.core.enums.file;

/**
 * @author NovaFox161
 * Date Created: 12/15/18
 * For Project: TAP-Backend
 * Author Website: https://www.novamaday.com
 * Company Website: https://www.dreamexposure.org
 * Contact: nova@dreamexposure.org
 */
public enum MimeType {
    IMAGE("image", "img"), MEDIA("media", "media"), AUDIO("audio", "audio"), VIDEO("video", "video"), JSON("application/json", "text"), TEXT("text/plain", "text");
    
    private String formalName;
    private String folder;
    
    MimeType(String _formalName, String _folder) {
        formalName = _formalName;
        folder = _folder;
    }
    
    public String getFormalName() {
        return formalName;
    }
    
    public String getFolder() {
        return folder;
    }}
