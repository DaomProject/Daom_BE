package com.daom.service;

import com.daom.domain.Student;
import com.daom.domain.UploadFile;
import com.daom.exception.NoSuchStudentException;
import com.daom.repository.StudentRepository;
import com.daom.repository.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
    private final FileStorage fileStorage;
    private final UploadFileRepository uploadFileRepository;
    private final StudentRepository studentRepository;
    @Transactional
    public void profileUpload(Long studentId, MultipartFile thumbnail) {
        Student student = studentRepository.findById(studentId).orElseThrow(NoSuchStudentException::new);
        UploadFile uploadedThumbnail = fileStorage.storeFile(thumbnail);

        student.addThumbnail(uploadedThumbnail);

    }
}
