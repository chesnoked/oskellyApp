//
//  Product.swift
//
//  Created by Виталий Хлудеев on 06.11.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Product: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let id = "id"
    static let sellerId = "sellerId"
    static let likesCount = "likesCount"
    static let seller = "seller"
    static let avatar = "avatar"
    static let city = "city"
    static let largeImages = "largeImages"
    static let descriptionValue = "description"
    static let size = "size"
    static let smallImages = "smallImages"
    static let category = "category"
    static let condition = "condition"
    static let brand = "brand"
    static let attributes = "attributes"
    static let offerRelated = "offerRelated"
    static let ourChoice = "ourChoice"
    static let doILike = "doILike"
    static let registrationDate = "registrationDate"
    static let reasonWhyItCannotBeAddedToCart = "reasonWhyItCannotBeAddedToCart"
    static let price = "price"
    static let startPrice = "startPrice"
    static let currentPrice = "currentPrice"
    static let pro = "pro"
    static let inMyWishList = "inMyWishList"
    static let canBeAddedToCart = "canBeAddedToCart"
    static let doIWatchOutForPrice = "doIWatchOutForPrice"
    static let hasDiscount = "hasDiscount"
  }

  // MARK: Properties
  public var id: Int!
  public var sellerId: Int?
  public var likesCount: Int!
  public var seller: String?
  public var avatar: String?
  public var city: String?
  public var largeImages: [String]!
  public var descriptionValue: String?
  public var size: ProductSize?
  public var smallImages: [String]?
  public var category: String?
  public var condition: String?
  public var brand: String?
  public var attributes: [ProductAttribute]?
  public var offerRelated: Offer?
  public var ourChoice: Bool? = false
  public var doILike: Bool! = false
  public var pro: Bool? = false
  public var inMyWishList: Bool! = false
  public var canBeAddedToCart: Bool? = false
  public var doIWatchOutForPrice: Bool! = false
  public var hasDiscount: Bool? = false
  public var registrationDate: String?
  public var reasonWhyItCannotBeAddedToCart: String?
  public var price: String?
  public var startPrice: String?
  public var currentPrice: String?

  // MARK: SwiftyJSON Initializers
  /// Initiates the instance based on the object.
  ///
  /// - parameter object: The object of either Dictionary or Array kind that was passed.
  /// - returns: An initialized instance of the class.
  public convenience init(object: Any) {
    self.init(json: JSON(object))
  }

  /// Initiates the instance based on the JSON that was passed.
  ///
  /// - parameter json: JSON object from SwiftyJSON.
  public required init(json: JSON) {
    id = json[SerializationKeys.id].int
    sellerId = json[SerializationKeys.sellerId].int
    likesCount = json[SerializationKeys.likesCount].int
    seller = json[SerializationKeys.seller].string
    avatar = json[SerializationKeys.avatar].string
    city = json[SerializationKeys.city].string
    if let items = json[SerializationKeys.largeImages].array { largeImages = items.map { $0.stringValue } }
    descriptionValue = json[SerializationKeys.descriptionValue].string
    size = ProductSize(json: json[SerializationKeys.size])
    if let items = json[SerializationKeys.smallImages].array { smallImages = items.map { $0.stringValue } }
    category = json[SerializationKeys.category].string
    condition = json[SerializationKeys.condition].string
    brand = json[SerializationKeys.brand].string
    if let items = json[SerializationKeys.attributes].array { attributes = items.map { ProductAttribute(json: $0) } }
    ourChoice = json[SerializationKeys.ourChoice].boolValue
    doILike = json[SerializationKeys.doILike].boolValue
    pro = json[SerializationKeys.pro].boolValue
    inMyWishList = json[SerializationKeys.inMyWishList].boolValue
    canBeAddedToCart = json[SerializationKeys.canBeAddedToCart].boolValue
    doIWatchOutForPrice = json[SerializationKeys.doIWatchOutForPrice].boolValue
    hasDiscount = json[SerializationKeys.hasDiscount].boolValue
    registrationDate = json[SerializationKeys.registrationDate].string
    reasonWhyItCannotBeAddedToCart = json[SerializationKeys.reasonWhyItCannotBeAddedToCart].string
    price = json[SerializationKeys.price].string
    startPrice = json[SerializationKeys.startPrice].string
    currentPrice = json[SerializationKeys.currentPrice].string
    offerRelated = Offer(json: json[SerializationKeys.offerRelated])
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = id { dictionary[SerializationKeys.id] = value }
    if let value = sellerId { dictionary[SerializationKeys.sellerId] = value }
    if let value = likesCount { dictionary[SerializationKeys.likesCount] = value }
    if let value = seller { dictionary[SerializationKeys.seller] = value }
    if let value = avatar { dictionary[SerializationKeys.avatar] = value }
    if let value = city { dictionary[SerializationKeys.city] = value }
    if let value = largeImages { dictionary[SerializationKeys.largeImages] = value }
    if let value = descriptionValue { dictionary[SerializationKeys.descriptionValue] = value }
    if let value = size { dictionary[SerializationKeys.size] = value.dictionaryRepresentation() }
    if let value = smallImages { dictionary[SerializationKeys.smallImages] = value }
    if let value = category { dictionary[SerializationKeys.category] = value }
    if let value = condition { dictionary[SerializationKeys.condition] = value }
    if let value = brand { dictionary[SerializationKeys.brand] = value }
    if let value = attributes { dictionary[SerializationKeys.attributes] = value.map { $0.dictionaryRepresentation() } }
    dictionary[SerializationKeys.ourChoice] = ourChoice
    dictionary[SerializationKeys.doILike] = doILike
    dictionary[SerializationKeys.pro] = pro
    dictionary[SerializationKeys.inMyWishList] = inMyWishList
    dictionary[SerializationKeys.canBeAddedToCart] = canBeAddedToCart
    dictionary[SerializationKeys.doIWatchOutForPrice] = doIWatchOutForPrice
    dictionary[SerializationKeys.hasDiscount] = hasDiscount
    if let value = registrationDate { dictionary[SerializationKeys.registrationDate] = value }
    if let value = reasonWhyItCannotBeAddedToCart { dictionary[SerializationKeys.reasonWhyItCannotBeAddedToCart] = value }
    if let value = price { dictionary[SerializationKeys.price] = value }
    if let value = startPrice { dictionary[SerializationKeys.startPrice] = value }
    if let value = currentPrice { dictionary[SerializationKeys.currentPrice] = value }
    if let value = offerRelated { dictionary[SerializationKeys.offerRelated] = value.dictionaryRepresentation() }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.id = aDecoder.decodeObject(forKey: SerializationKeys.id) as? Int
    self.sellerId = aDecoder.decodeObject(forKey: SerializationKeys.sellerId) as? Int
    self.likesCount = aDecoder.decodeObject(forKey: SerializationKeys.likesCount) as? Int
    self.seller = aDecoder.decodeObject(forKey: SerializationKeys.seller) as? String
    self.avatar = aDecoder.decodeObject(forKey: SerializationKeys.avatar) as? String
    self.city = aDecoder.decodeObject(forKey: SerializationKeys.city) as? String
    self.largeImages = aDecoder.decodeObject(forKey: SerializationKeys.largeImages) as? [String]
    self.descriptionValue = aDecoder.decodeObject(forKey: SerializationKeys.descriptionValue) as? String
    self.size = aDecoder.decodeObject(forKey: SerializationKeys.size) as? ProductSize
    self.smallImages = aDecoder.decodeObject(forKey: SerializationKeys.smallImages) as? [String]
    self.category = aDecoder.decodeObject(forKey: SerializationKeys.category) as? String
    self.condition = aDecoder.decodeObject(forKey: SerializationKeys.condition) as? String
    self.brand = aDecoder.decodeObject(forKey: SerializationKeys.brand) as? String
    self.attributes = aDecoder.decodeObject(forKey: SerializationKeys.attributes) as? [ProductAttribute]
    self.ourChoice = aDecoder.decodeBool(forKey: SerializationKeys.ourChoice)
    self.doILike = aDecoder.decodeBool(forKey: SerializationKeys.doILike)
    self.pro = aDecoder.decodeBool(forKey: SerializationKeys.pro)
    self.inMyWishList = aDecoder.decodeBool(forKey: SerializationKeys.inMyWishList)
    self.canBeAddedToCart = aDecoder.decodeBool(forKey: SerializationKeys.canBeAddedToCart)
    self.doIWatchOutForPrice = aDecoder.decodeBool(forKey: SerializationKeys.doIWatchOutForPrice)
    self.hasDiscount = aDecoder.decodeBool(forKey: SerializationKeys.hasDiscount)
    self.registrationDate = aDecoder.decodeObject(forKey: SerializationKeys.registrationDate) as? String
    self.reasonWhyItCannotBeAddedToCart = aDecoder.decodeObject(forKey: SerializationKeys.reasonWhyItCannotBeAddedToCart) as? String
    self.price = aDecoder.decodeObject(forKey: SerializationKeys.price) as? String
    self.startPrice = aDecoder.decodeObject(forKey: SerializationKeys.startPrice) as? String
    self.currentPrice = aDecoder.decodeObject(forKey: SerializationKeys.currentPrice) as? String
    self.offerRelated = aDecoder.decodeObject(forKey: SerializationKeys.offerRelated) as? Offer
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(seller, forKey: SerializationKeys.seller)
    aCoder.encode(avatar, forKey: SerializationKeys.avatar)
    aCoder.encode(city, forKey: SerializationKeys.city)
    aCoder.encode(largeImages, forKey: SerializationKeys.largeImages)
    aCoder.encode(descriptionValue, forKey: SerializationKeys.descriptionValue)
    aCoder.encode(size, forKey: SerializationKeys.size)
    aCoder.encode(smallImages, forKey: SerializationKeys.smallImages)
    aCoder.encode(category, forKey: SerializationKeys.category)
    aCoder.encode(condition, forKey: SerializationKeys.condition)
    aCoder.encode(brand, forKey: SerializationKeys.brand)
    aCoder.encode(attributes, forKey: SerializationKeys.attributes)
    aCoder.encode(ourChoice, forKey: SerializationKeys.ourChoice)
    aCoder.encode(pro, forKey: SerializationKeys.pro)
    aCoder.encode(inMyWishList, forKey: SerializationKeys.inMyWishList)
    aCoder.encode(canBeAddedToCart, forKey: SerializationKeys.canBeAddedToCart)
    aCoder.encode(doIWatchOutForPrice, forKey: SerializationKeys.doIWatchOutForPrice)
    aCoder.encode(hasDiscount, forKey: SerializationKeys.hasDiscount)
    aCoder.encode(registrationDate, forKey: SerializationKeys.registrationDate)
    aCoder.encode(reasonWhyItCannotBeAddedToCart, forKey: SerializationKeys.reasonWhyItCannotBeAddedToCart)
    aCoder.encode(price, forKey: SerializationKeys.price)
    aCoder.encode(startPrice, forKey: SerializationKeys.startPrice)
    aCoder.encode(currentPrice, forKey: SerializationKeys.currentPrice)
    aCoder.encode(likesCount, forKey: SerializationKeys.likesCount)
    aCoder.encode(offerRelated, forKey: SerializationKeys.offerRelated)
  }
}
