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
    private final StudentRepository studentRepository;
    @Transactional
    public void profileUpload(Long studentId, MultipartFile thumbnail) {
        Student student = studentRepository.findById(studentId).orElseThrow(NoSuchStudentException::new);
        if(student.getThumbnailName()!=null){
            profileDelete(studentId);
        }
        UploadFile uploadedThumbnail = fileStorage.storeFile(thumbnail);

        student.addThumbnail(uploadedThumbnail);

    }
    @Transactional
    public void profileDelete(Long studentId){
        Student student = studentRepository.findById(studentId).orElseThrow(NoSuchStudentException::new);
        //student의 id로 thumnail을 가져오는 함수가 필요

        fileStorage.deleteFile(student.getThumbnailName());
        student.deleteThumbnail();

    }
}
