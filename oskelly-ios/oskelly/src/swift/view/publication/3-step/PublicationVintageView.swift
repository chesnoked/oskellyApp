//
// Created by Виталий Хлудеев on 25.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class PublicationVintageView : UITableViewCell {

    let vintage = UISwitch()

    let vintageLabel = UILabel()

    let vintageDescriptionLabel = UILabel()

    let separator = UIView()

    var draft: Draft!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)

        draft = GlobalProvider.instance.draftProvider.getCurrent()

        vintage.isOn = draft.vintage != nil && draft.vintage!

        self.addSubview(vintageLabel)
        vintageLabel.snp.makeConstraints { m in
            m.left.equalTo(self).inset(10)
            m.top.equalTo(self).inset(14)
        }
        vintageLabel.text = "Винтаж"
        vintageLabel.font = BoldFont.systemFont(ofSize: 13)

        self.addSubview(vintage)
        vintage.snp.makeConstraints { m in
            m.centerY.equalTo(vintageLabel)
            m.right.equalTo(self).inset(10)
            m.height.equalTo(15)
        }

        self.addSubview(separator)
        separator.backgroundColor = AppColors.separator()
        separator.snp.makeConstraints { m in
            m.height.equalTo(1)
            m.bottom.equalTo(self)
            m.left.equalTo(self)
            m.right.equalTo(self)
        }

        self.addSubview(vintageDescriptionLabel)
        vintageDescriptionLabel.text = "Возраст вещи составляет 15 и более лет"
        vintageDescriptionLabel.snp.makeConstraints { m in
            m.top.equalTo(vintageLabel.snp.bottom).offset(5)
            m.left.equalTo(vintageLabel)
        }
        vintageDescriptionLabel.font = MediumFont.systemFont(ofSize: 10)
        vintageDescriptionLabel.textColor = .gray

        vintage.addTarget(self, action: #selector(switchChanged), for: UIControlEvents.valueChanged)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func switchChanged(mySwitch: UISwitch) {
        let value = mySwitch.isOn
        draft.vintage = value
        GlobalProvider.instance.draftProvider.setCurrent(draft: draft)
        GlobalProvider.instance.draftProvider.publish(draft: draft) {d in}
    }
}