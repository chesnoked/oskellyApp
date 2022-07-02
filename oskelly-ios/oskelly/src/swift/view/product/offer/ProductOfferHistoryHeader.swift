//
// Created by Виталий Хлудеев on 10.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductOfferHistoryHeader : UITableViewCell {

    private let offerHistory = UIView()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addOfferHistory()
    }

    private func addOfferHistory() {
        contentView.addSubview(offerHistory)
        offerHistory.backgroundColor = AppColors.grayLabel()
        offerHistory.snp.makeConstraints({m in
            m.height.equalTo(40)
            m.edges.equalTo(contentView).inset(UIEdgeInsetsMake(0, 0, 17, 0))
        })

        let l = UILabel()
        offerHistory.addSubview(l)
        l.text = "ИСТОРИЯ ПЕРЕГОВОРОВ:"
        l.textAlignment = .center
        l.textColor = AppColors.grayLabelText()
        l.font = MediumFont.systemFont(ofSize: 14)
        l.snp.makeConstraints({m in
            m.center.equalTo(offerHistory)
        })
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}