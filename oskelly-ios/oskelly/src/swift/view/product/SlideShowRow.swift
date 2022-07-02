//
// Created by Виталий Хлудеев on 24.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit
import ImageSlideshow
import SnapKit

class SlideShowRow : UITableViewCell {

    private let slideshow = ImageSlideshow()
    private let likeButton = LikeButton()
    private let likesCountLabel = UILabel()

    private let urls = [
            "http://oskelly.ml/img/3850434-1_1",
            "http://oskelly.ml/img/3850434-3_1",
            "http://oskelly.ml/img/3851824-1_1",
            "http://oskelly.ml/img/3855074-1_1"
    ]

    private var product: Product!

    override init(style: UITableViewCellStyle, reuseIdentifier: String!) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        addSlideShow(container: self.contentView)
//        addProductLabels(container: self.contentView)
        addLikeElements()
    }

    private func addLikeElements() {
        contentView.addSubview(likeButton)
        likeButton.snp.makeConstraints { m in
            m.right.equalTo(contentView).inset(20)
            m.top.equalTo(contentView).inset(30)
            m.width.equalTo(25)
            m.height.equalTo(25)
        }
        likeButton.addTarget(self, action: #selector(self.like), for: .touchUpInside)

        contentView.addSubview(likesCountLabel)
        likesCountLabel.text = "157"
        likesCountLabel.textAlignment = .center
        likesCountLabel.font = MediumFont.systemFont(ofSize: 10)
        likesCountLabel.snp.makeConstraints({m in
            m.centerX.equalTo(likeButton)
            m.top.equalTo(likeButton.snp.bottom).offset(3)
        })
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func render(product: Product) {
        self.product = product
        let alamofireSource = product.largeImages.map {(url) -> AlamofireSource in
            return AlamofireSource(urlString: ApiRequester.domain + url)!
        }
        slideshow.setImageInputs(alamofireSource)

        likesCountLabel.text = String(product.likesCount!)
        likeButton.like(isLike: product.doILike!)
    }

    func addSlideShow(container: UIView) {
        slideshow.backgroundColor = .white
        slideshow.pageControlPosition = PageControlPosition.underScrollView
        slideshow.pageControl.currentPageIndicatorTintColor = .black
        slideshow.pageControl.pageIndicatorTintColor = .lightGray
        slideshow.contentScaleMode = UIViewContentMode.scaleAspectFit
        slideshow.activityIndicator = DefaultActivityIndicator()
        let alamofireSource = urls.map {(url) -> AlamofireSource in
            return AlamofireSource(urlString: url)!
        }
        slideshow.setImageInputs(alamofireSource)
        container.addSubview(slideshow)
        slideshow.snp.makeConstraints { (make) -> Void in
            make.width.equalTo(container.snp.width)
            make.height.equalTo(container.snp.width).dividedBy(0.99)
            make.top.equalTo(container.snp.top).inset(20)
            make.edges.equalTo(contentView).inset(UIEdgeInsetsMake(0, 0, 0, 0))
        }
    }

    func addProductLabels(container: UIView) {
        let ourChoiceLabel = OurChoiceLabel()
        ourChoiceLabel.font = UIFont.boldSystemFont(ofSize: 10)
        container.addSubview(ourChoiceLabel)
        ourChoiceLabel.snp.makeConstraints { (make) -> Void in
            make.top.equalTo(slideshow.snp.bottom).inset(37)
            make.width.equalTo(UIScreen.main.bounds.width/4.5)
            make.height.equalTo(UIScreen.main.bounds.width / 25)
            make.left.equalTo(container.snp.left).offset(20)
        }

        let separator = UIView()
        separator.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0)
        container.addSubview(separator)
        separator.snp.makeConstraints { (make) -> Void in
            make.height.equalTo(1)
            make.left.equalTo(ourChoiceLabel.snp.left)
            make.width.equalTo(ourChoiceLabel.snp.width)
            make.bottom.equalTo(ourChoiceLabel.snp.top)
        }

        let inTheSockLabel = InTheStockLabel()
        inTheSockLabel.font = UIFont.boldSystemFont(ofSize: 10)
        container.addSubview(inTheSockLabel)
        inTheSockLabel.snp.makeConstraints { (make) -> Void in
            make.bottom.equalTo(separator.snp.top)
            make.width.equalTo(separator.snp.width)
            make.height.equalTo(ourChoiceLabel.snp.height)
            make.left.equalTo(separator.snp.left)
        }
    }

    func like() {
        let isLike = !product.doILike
        GlobalProvider.instance.getApiRequester().likeToggle(productId: product.id)
        product.doILike = isLike
        if(isLike) {
            product.likesCount! += 1
        }
        else {
            product.likesCount! -= 1
        }
        likeButton.like(isLike: isLike)
        likesCountLabel.text = String(product.likesCount)
    }
}