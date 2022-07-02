//
// Created by Виталий Хлудеев on 09.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProfilePhotoView: UIImageView {

    init() {
        super.init(image: nil)
        setDefaultImage()
        self.contentMode = .scaleAspectFit
        self.layer.masksToBounds = true
        self.layer.borderWidth = 1
        self.layer.borderColor = UIColor.lightGray.cgColor
    }

    func setDefaultImage() {
        let url = URL(string: ApiRequester.domain + "/images/no-photo.jpg")!
        self.af_setImage(withURL: url)
    }

    func setImage(uri: String?) {
        setDefaultImage()
        uri.map({
            let url = URL(string: ApiRequester.domain + $0)!
            self.af_setImage(withURL: url)
        })
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}