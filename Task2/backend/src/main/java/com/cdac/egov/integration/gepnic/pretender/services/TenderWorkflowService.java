package com.cdac.egov.integration.gepnic.pretender.services;

import java.util.ArrayList;
import java.util.List;
import com.cdac.egov.integration.gepnic.pretender.forms.TenderRequest;
import com.cdac.egov.integration.gepnic.pretender.forms.TenderResponse;
import com.cdac.egov.integration.gepnic.pretender.forms.UploadedFileResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Coordinates parent-work verification and temporary file staging around tender persistence. */
@Service
public class TenderWorkflowService {
    private final ParentWorkSelectionService parentWorks;
    private final TenderService tenders;
    private final FileStorageService files;

    public TenderWorkflowService(ParentWorkSelectionService parentWorks, TenderService tenders, FileStorageService files) {
        this.parentWorks = parentWorks;
        this.tenders = tenders;
        this.files = files;
    }

    public TenderResponse create(TenderRequest request) {
        return tenders.create(parentWorks.resolve(request));
    }

    public TenderResponse update(long id, TenderRequest request) {
        return tenders.update(id, parentWorks.resolve(request));
    }

    public TenderResponse createWithFiles(TenderRequest request, MultipartFile nit, MultipartFile dtcn, MultipartFile other) {
        return saveWithFiles(request, null, nit, dtcn, other);
    }

    public TenderResponse updateWithFiles(long id, TenderRequest request, MultipartFile nit, MultipartFile dtcn, MultipartFile other) {
        return saveWithFiles(request, id, nit, dtcn, other);
    }

    private TenderResponse saveWithFiles(TenderRequest request, Long id, MultipartFile nit, MultipartFile dtcn, MultipartFile other) {
        TenderRequest resolved = parentWorks.resolve(request);
        TenderResponse previous = id == null ? null : tenders.get(id);
        List<String> stagedNames = new ArrayList<>();
        try {
            String nitName = stage(nit, stagedNames);
            String dtcnName = stage(dtcn, stagedNames);
            String otherName = stage(other, stagedNames);
            TenderRequest withFiles = resolved.withFileNames(nitName, dtcnName, otherName);
            TenderResponse saved = id == null ? tenders.create(withFiles) : tenders.update(id, withFiles);
            if (previous != null) {
                deleteReplaced(previous.nitFileName(), saved.nitFileName());
                deleteReplaced(previous.dtcnFileName(), saved.dtcnFileName());
                deleteReplaced(previous.otherFileName(), saved.otherFileName());
            }
            return saved;
        } catch (RuntimeException ex) {
            stagedNames.forEach(files::deleteQuietly);
            throw ex;
        }
    }

    private String stage(MultipartFile file, List<String> stagedNames) {
        if (file == null || file.isEmpty()) return null;
        UploadedFileResponse stored = files.store(file);
        stagedNames.add(stored.fileName());
        return stored.fileName();
    }

    private void deleteReplaced(String previous, String current) {
        if (previous != null && !previous.isBlank() && !previous.equals(current)) files.deleteQuietly(previous);
    }
}
