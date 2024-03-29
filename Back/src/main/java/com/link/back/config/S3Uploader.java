package com.link.back.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.link.back.dto.Image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor    // final 멤버변수가 있으면 생성자 항목에 포함시킴
@Component
public class S3Uploader {
	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	// MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
	public Image upload(MultipartFile multipartFile, String dirName) throws IOException {
		File uploadFile = convert(multipartFile)
			.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
		String originalName = multipartFile.getOriginalFilename();
		return upload(uploadFile, dirName, originalName);
	}

	private Image upload(File uploadFile, String dirName, String originalName) {
		String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();
		String uploadImageUrl = putS3(uploadFile, fileName);

		removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)
		return new Image(fileName, uploadImageUrl, originalName);      // 업로드된 파일의 S3 URL 주소 반환
	}

	private String putS3(File uploadFile, String fileName) {
		amazonS3.putObject(
			new PutObjectRequest(bucket, fileName, uploadFile)
				.withCannedAcl(CannedAccessControlList.PublicRead)    // PublicRead 권한으로 업로드 됨
		);
		return amazonS3.getUrl(bucket, fileName).toString();
	}

	private void removeNewFile(File targetFile) {
		targetFile.delete();
	}

	private Optional<File> convert(MultipartFile file) throws IOException {
		File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
		if (!convertFile.exists()) {
			if (convertFile.createNewFile()) {
				System.out.println("hi");
				try (FileOutputStream fos = new FileOutputStream(convertFile)) {
					fos.write(file.getBytes());
				}
				return Optional.of(convertFile);
			}
		} else {
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}

	//remove s3 object
	public void deleteFile(String fileName){
		DeleteObjectRequest request = new DeleteObjectRequest(bucket, fileName);
		amazonS3.deleteObject(request);
	}

}
