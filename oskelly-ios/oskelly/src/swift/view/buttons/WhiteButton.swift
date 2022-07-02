//
//  WhiteButton.swift
//  oskelly
//
//  Created by Виталий Хлудеев on 07.06.17.
//  Copyright © 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class WhiteButton: UIButton {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = .white
        setTitleColor(.black, for: .normal)
        setTitleColor(.gray, for: .highlighted)

        titleLabel?.font = BlackFont.systemFont(ofSize: 14)
        layer.borderWidth = 1
        layer.borderColor = UIColor.black.cgColor
        self.snp.makeConstraints { make in
            make.height.equalTo(50)
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
