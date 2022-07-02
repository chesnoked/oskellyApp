//
// Created by Виталий Хлудеев on 15.05.17.
// Copyright (c) 2017 Виталий Хлудеев. All rights reserved.
//

import Foundation
import UIKit

class ProductCollectionItemCell : UICollectionViewCell {
    
    private let imageView: UIImageView = UIImageView()
    private let inTheStockLabel = InTheStockLabel()
    private let brandNameLabel = UILabel()
    private let ourChoiceLabel = OurChoiceLabel()
    private let productNameLabel = UILabel()
    private let sizeLabel = UILabel()
    private let priceLabel = UILabel()
    private let neverInUseLabel = NeverInUseLabel()
    private let likesCountLabel = UILabel()
    private let likeButton = LikeButton()

    override init(frame: CGRect) {
        super.init(frame: frame)

        let cell = self
        cell.backgroundColor = .white

        let topLabelsView = self.addTopLabelsView(cell: self)

        let imageViewYPosition = topLabelsView.bounds.height * 0.5
        imageView.frame = CGRect(x: 12, y: imageViewYPosition, width: cell.bounds.width - 18, height: cell.bounds.height * 0.7)

        imageView.contentMode = UIViewContentMode.scaleAspectFit
        cell.contentView.addSubview(imageView)

        let inStockLabelYPosition = imageViewYPosition + imageView.bounds.height * 0.7
        inTheStockLabel.frame = CGRect(x: 8, y: inStockLabelYPosition, width: cell.bounds.width / 2.5, height: cell.bounds.width / (2.5 * 4.5))
        inTheStockLabel.layer.zPosition = 1;
        cell.contentView.addSubview(inTheStockLabel)

        let ourChoiceLabelYPosition = inStockLabelYPosition + inTheStockLabel.bounds.height + 1
        ourChoiceLabel.frame = CGRect(x: 8, y: ourChoiceLabelYPosition, width: cell.bounds.width / 2.5, height: cell.bounds.width / (2.5 * 4.5))
        ourChoiceLabel.layer.zPosition = 1;
        cell.contentView.addSubview(ourChoiceLabel)

        let brandNameYPosition = ourChoiceLabelYPosition + ourChoiceLabel.bounds.height * 2.5
        brandNameLabel.frame = CGRect(x: 0, y: brandNameYPosition, width: cell.bounds.width, height: cell.bounds.width * 0.1)
        brandNameLabel.textAlignment = .center
        brandNameLabel.layer.zPosition = 1;
        brandNameLabel.font = BlackFont.systemFont(ofSize: 11)
        cell.contentView.addSubview(brandNameLabel)

        let productLabelYPosition = brandNameYPosition + brandNameLabel.bounds.height + 4
        productNameLabel.frame = CGRect(x: 0, y: productLabelYPosition, width: cell.bounds.width, height: cell.bounds.width * 0.09)
        productNameLabel.textAlignment = .center
        productNameLabel.font = BoldFont.systemFont(ofSize: 9)
        productNameLabel.textColor = .lightGray
        cell.contentView.addSubview(productNameLabel)

        let sizeLabelYPosition = productLabelYPosition + productNameLabel.bounds.height + 4
        sizeLabel.frame = CGRect(x: 0, y: sizeLabelYPosition, width: cell.bounds.width, height: cell.bounds.width * 0.09)
        sizeLabel.textAlignment = .center
        sizeLabel.font = BoldFont.systemFont(ofSize: 9)
        sizeLabel.textColor = .lightGray
        cell.contentView.addSubview(sizeLabel)

        let priceLabelYPosition = sizeLabelYPosition + sizeLabel.bounds.height + 5
        priceLabel.frame = CGRect(x: 0, y: priceLabelYPosition, width: cell.bounds.width, height: cell.bounds.width * 0.1)
        priceLabel.textAlignment = .center
        priceLabel.text = "250088 Р"
        priceLabel.font = MediumFont.systemFont(ofSize: 11)
        cell.contentView.addSubview(priceLabel)

        let stripeView = UIView(frame: CGRect(x: 12, y: cell.bounds.height - 1, width: cell.bounds.width - 24, height: 1))
        stripeView.backgroundColor = UIColor.lightGray
        cell.contentView.addSubview(stripeView)
    }

    private func addTopLabelsView(cell: UICollectionViewCell) -> UIView {
        let topLabelsView = UIView(frame: CGRect(x: 0, y: 0, width: cell.bounds.width, height: cell.bounds.height * 0.2 + 2))
        let diameter = topLabelsView.bounds.height * 0.6
        neverInUseLabel.setRadius(radius: diameter / 2)
        let neverInUseLabelTopPosition = topLabelsView.bounds.height - diameter
        neverInUseLabel.frame = CGRect(x: 8, y: neverInUseLabelTopPosition, width: diameter, height: diameter)
        topLabelsView.layer.zPosition = 1;
        topLabelsView.addSubview(neverInUseLabel)
        cell.contentView.addSubview(topLabelsView)

        let likesImageWidth = neverInUseLabel.bounds.width / 1.9
        let likeImageViewLeftPosition = topLabelsView.bounds.maxX - likesImageWidth - 7

        let likeButtonTopPosition = neverInUseLabelTopPosition + 1
        likeButton.frame = CGRect(x: likeImageViewLeftPosition, y: likeButtonTopPosition, width: likesImageWidth, height: likesImageWidth)
        topLabelsView.addSubview(likeButton)

        likesCountLabel.text = "157"
        likesCountLabel.textAlignment = .center
        likesCountLabel.font = MediumFont.systemFont(ofSize: 6)
        likesCountLabel.frame = CGRect(x: likeImageViewLeftPosition, y: likeButtonTopPosition + likesImageWidth + 2, width: likesImageWidth, height: 10)
        topLabelsView.addSubview(likesCountLabel)

        return topLabelsView
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    func render(item: ProductCollectionItem) {
        let placeholderImage = UIImage(named: "assets/images/publication/DraftPlaceHolder.png")!
        if let imageUrl = item.image {
            let url = URL(string: ApiRequester.domain + imageUrl)!
            imageView.af_setImage(withURL: url, placeholderImage: placeholderImage)
        }
        else {
            imageView.image = placeholderImage
        }
        neverInUseLabel.isHidden = !item.notUsedYet!
        ourChoiceLabel.isHidden = true
        inTheStockLabel.isHidden = true
        brandNameLabel.text = item.brand
        productNameLabel.text = item.category
        let sizeValuesString = item.size?.values?.map({ $0.value! }).joined(separator: "; ")
        if let sizeType = item.size?.type  {
            sizeLabel.text = "Размер: " + sizeType + " " + sizeValuesString!
        }
        else {
            sizeLabel.text = "Без размера"
        }
        likesCountLabel.text = String(item.likesCount!)

        if let price = item.price {
            priceLabel.text = String(price) + " ₽"
        }
        else {
            priceLabel.text = "Цена не указана"
        }
        priceLabel.textColor = (item.hasDiscount ?? false) ? .red : .black
        likeButton.like(isLike: item.doILike!)

        likesCountLabel.text = String(item.likesCount ?? 0)
        likeButton.like(isLike: item.doILike ?? false)
    }
}
