//
// Created by Виталий Хлудеев on 22.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import MobileCoreServices
import UIKit
import DWImagePicker_Swift3

//https://stackoverflow.com/questions/39812390/how-to-load-image-from-camera-or-photo-library-in-swift
class DSCameraHandler: NSObject {

    private let imagePicker = WDImagePicker()
    private let isPhotoLibraryAvailable = UIImagePickerController.isSourceTypeAvailable(.photoLibrary)
    private let isSavedPhotoAlbumAvailable = UIImagePickerController.isSourceTypeAvailable(.savedPhotosAlbum)
    private let isCameraAvailable = UIImagePickerController.isSourceTypeAvailable(.camera)
    private let isRearCameraAvailable = UIImagePickerController.isCameraDeviceAvailable(.rear)
    private let isFrontCameraAvailable = UIImagePickerController.isCameraDeviceAvailable(.front)
    private let sourceTypeCamera = UIImagePickerControllerSourceType.camera
    private let rearCamera = UIImagePickerControllerCameraDevice.rear
    private let frontCamera = UIImagePickerControllerCameraDevice.front

    var delegate: WDImagePickerDelegate
    init(delegate_: WDImagePickerDelegate) {
        delegate = delegate_
        imagePicker.resizableCropArea = true
    }

    func getPhotoLibraryOn(_ onVC: UIViewController, canEdit: Bool) {

        if !isPhotoLibraryAvailable && !isSavedPhotoAlbumAvailable { return }
        let type = kUTTypeImage as String

        if isPhotoLibraryAvailable {
            imagePicker.imagePickerController.sourceType = .photoLibrary
            if let availableTypes = UIImagePickerController.availableMediaTypes(for: .photoLibrary) {
                if availableTypes.contains(type) {
                    imagePicker.imagePickerController.mediaTypes = [type]
                    imagePicker.imagePickerController.allowsEditing = canEdit
                }
            }
        } else if isPhotoLibraryAvailable {
            imagePicker.imagePickerController.sourceType = .savedPhotosAlbum
            if let availableTypes = UIImagePickerController.availableMediaTypes(for: .savedPhotosAlbum) {
                if availableTypes.contains(type) {
                    imagePicker.imagePickerController.mediaTypes = [type]
                }
            }
        } else {
            return
        }

        imagePicker.imagePickerController.allowsEditing = canEdit
        imagePicker.delegate = delegate
        onVC.present(imagePicker.imagePickerController, animated: true, completion: nil)
    }

    func getCameraOn(_ onVC: UIViewController, canEdit: Bool) {

        if !isCameraAvailable { return }
        let type1 = kUTTypeImage as String

        if isCameraAvailable {
            if let availableTypes = UIImagePickerController.availableMediaTypes(for: .camera) {
                if availableTypes.contains(type1) {
                    imagePicker.imagePickerController.mediaTypes = [type1]
                    imagePicker.imagePickerController.sourceType = sourceTypeCamera
                }
            }

            if isRearCameraAvailable {
                imagePicker.imagePickerController.cameraDevice = rearCamera
            } else if isFrontCameraAvailable {
                imagePicker.imagePickerController.cameraDevice = frontCamera
            }
        } else {
            return
        }

        imagePicker.imagePickerController.allowsEditing = canEdit
        imagePicker.imagePickerController.showsCameraControls = true
        imagePicker.delegate = delegate
        onVC.present(imagePicker.imagePickerController, animated: true, completion: nil)
    }
}