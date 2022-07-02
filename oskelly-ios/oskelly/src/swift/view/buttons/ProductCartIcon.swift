//
// Created by Виталий Хлудеев on 25.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductCartIcon : UIButton {

    var controller: UIViewController?

    override init(frame: CGRect) {
        super.init(frame: frame)
        setImage(UIImage(named: "assets/images/navigation/ProductCart.png"), for: .normal)
        imageView?.contentMode = .scaleAspectFit
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}