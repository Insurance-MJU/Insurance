package domain;

import java.util.Date;

public class ClaimDocument {
    private String claimId;
    private String documentId;
    private String documentName;
    private DocumentType documentType;
    private String filePath;
    private Date uploadDate;
    private boolean verified;

    public enum DocumentType {
        ACCIDENT_PHOTO,      // 사고 현장 사진
        POLICE_REPORT,       // 경찰 사고 확인서
        MEDICAL_CERTIFICATE, // 진단서
        REPAIR_ESTIMATE,     // 수리 견적서
        CLAIM_FORM,          // 보험금 청구서
        ASSESSMENT_OPINION,  // 사정의견서 (CL-04 결재용)
        INVESTIGATION_REPORT // 손해 조사 보고서 (CL-03)
    }

    public String getDocument() { return null; }
    public boolean uploadDocument(String filePath) { return false; }
    public boolean verifyDocument() { return false; }

    public String getClaimId() { return claimId; }
    public String getDocumentId() { return documentId; }
    public String getDocumentName() { return documentName; }
    public DocumentType getDocumentType() { return documentType; }
    public String getFilePath() { return filePath; }
    public Date getUploadDate() { return uploadDate; }
    public boolean isVerified() { return verified; }
}
