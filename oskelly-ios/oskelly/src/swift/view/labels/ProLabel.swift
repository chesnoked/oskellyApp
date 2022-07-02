//
// Created by Виталий Хлудеев on 09.11.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProLabel : UILabel {

    static let PRO_LABEL_WIDTH: CGFloat = 42.0

    static let PRO_LABEL_HEIGHT: CGFloat = 42.0 / 2.8

    init() {
        super.init(frame: CGRect())
        self.text = "PRO"
        self.textColor = .white
        self.backgroundColor = UIColor(red: 206/255, green: 167/255, blue: 138/255, alpha: 1)
        self.font = BoldFont.systemFont(ofSize: 10)
        self.textAlignment = .center
        self.layer.cornerRadius = ProLabel.PRO_LABEL_HEIGHT / 2
        self.layer.masksToBounds = true
        self.snp.makeConstraints({m in
            m.width.equalTo(ProLabel.PRO_LABEL_WIDTH)
            m.height.equalTo(ProLabel.PRO_LABEL_HEIGHT)
        })
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}