//
// Created by Виталий Хлудеев on 17.10.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class PublicationStepSummaryRow : UITableViewCell {

    let stepNameLabel: UILabel = UILabel()
    let stepNumberLabel = StepNumberLabel()
    let selectedStateImage = UIImage(named: "assets/images/catalog/Cheker.png")
    let selectStateImageView = UIImageView()

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addStepNumberLabel()

        contentView.addSubview(stepNameLabel)
        stepNameLabel.snp.makeConstraints { (make) -> Void in
            make.centerY.equalTo(stepNumberLabel)
            make.left.equalTo(stepNumberLabel.snp.right).offset(16)
        }
        stepNameLabel.font = MediumFont.systemFont(ofSize: 14)
        let separator = UIView()
        separator.backgroundColor = AppColors.separator()
        contentView.addSubview(separator)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1)
            make.bottom.equalTo(contentView)
            make.right.equalTo(contentView)
            make.left.equalTo(contentView)
        }

        selectStateImageView.image = selectedStateImage
        contentView.addSubview(selectStateImageView)
        selectStateImageView.snp.makeConstraints({m in
            m.edges.equalTo(stepNumberLabel)
        })
    }

    func render(stepNumber: Int, stepName: String, completed: Bool) {
        stepNumberLabel.text = String(stepNumber)
        stepNameLabel.text = stepName
        stepNumberLabel.isHidden = completed
        selectStateImageView.isHidden = !completed
    }

    private func addStepNumberLabel() {
        let height = contentView.bounds.height
        let offset: CGFloat = 7.0
        let diameter = height - (offset * 2)
        let radius = diameter / 2
        stepNumberLabel.frame = CGRect(x: offset * 2, y: offset * 2, width: diameter, height: diameter)
        stepNumberLabel.setRadius(radius: radius)
        contentView.addSubview(stepNumberLabel)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}