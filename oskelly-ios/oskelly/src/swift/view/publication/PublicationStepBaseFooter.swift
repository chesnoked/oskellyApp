//
// Created by Sergey Kultishev on 04.03.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class PublicationStepBaseFooter : UIView {

    static let height: CGFloat = 100.0

    let sellButton = DarkButton()
    var controller: UIViewController!
    var nextStepHandler: ((Void) -> ())?

    override init(frame: CGRect){
        super.init(frame: frame)
        addSubview(sellButton)
        sellButton.setTitle("Продолжить", for: .normal)
        sellButton.snp.makeConstraints { make -> Void in
            make.center.equalTo(self)
            make.width.equalTo(self).multipliedBy(0.9)
        }
        sellButton.addTarget(self, action: #selector(self.goToNextController(_:)), for: .touchUpInside)

    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func goToNextController(_ sender: Any){
        nextStepHandler?()
    }
}
