//
// Created by Виталий Хлудеев on 08.01.18.
// Copyright (c) 2018 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import SnapKit

class ProductAddCommentRow : UITableViewCell {

    private let commentButton = WhiteButton()

    private var controller: ProductViewController?
    private var product: Product!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        isUserInteractionEnabled = true
        contentView.addSubview(commentButton)
        commentButton.setTitle("Оставить комментарий", for: .normal)
        commentButton.snp.makeConstraints { make in
            make.left.equalTo(contentView).inset(15)
            make.right.equalTo(contentView).inset(15)
            make.top.equalTo(contentView).inset(18)
            make.bottom.equalTo(contentView).inset(10)
        }
        commentButton.addTarget(self, action: #selector(self.commentButtonClick), for: .touchUpInside)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func render(controller: ProductViewController, product: Product) {
        self.controller = controller
        self.product = product
    }

    func commentButtonClick() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let c = storyboard.instantiateViewController(withIdentifier: "ProductCommentController") as! ProductCommentController
        c.parentController = controller
        c.product = product
        controller?.navigationController?.pushViewController(c, animated: true)
    }
}