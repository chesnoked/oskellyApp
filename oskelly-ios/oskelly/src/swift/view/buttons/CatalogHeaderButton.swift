//
// Created by Виталий Хлудеев on 11.12.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class CatalogHeaderButton : UIButton {

    override init(frame: CGRect) {
        super.init(frame: frame)
        setTitleColor(.gray, for: .normal)
        titleLabel?.font = MediumFont.systemFont(ofSize: 14)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}