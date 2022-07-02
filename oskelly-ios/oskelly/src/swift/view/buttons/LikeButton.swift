//
// Created by Виталий Хлудеев on 06.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class LikeButton : UIButton {

    override init(frame: CGRect) {
        super.init(frame: frame)
        like(isLike: false)
        imageView?.contentMode = .scaleAspectFit
    }

    func like(isLike : Bool ) {
        if(isLike) {
            setImage(UIImage(named: "assets/images/LikeSelected.png"), for: .normal)
        }
        else {
            setImage(UIImage(named: "assets/images/LikeDeselected.png"), for: .normal)
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}