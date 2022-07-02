//
//  Sale.swift
//
//  Created by Виталий Хлудеев on 17.12.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class Sale: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let productId = "productId"
    static let buyPrice = "buyPrice"
    static let id = "id"
    static let productName = "productName"
    static let orderId = "orderId"
    static let stateName = "stateName"
    static let image = "image"
    static let size = "size"
    static let needSaleConfirm = "needSaleConfirm"
    static let brandName = "brandName"
    static let price = "price"
    static let buyPriceWithoutCommission = "buyPriceWithoutCommission"
  }

  // MARK: Properties
  public var productId: Int?
  public var buyPrice: Float?
  public var id: Int!
  public var productName: String?
  public var orderId: String?
  public var stateName: String?
  public var image: String?
  public var size: String?
  public var needSaleConfirm: Bool! = false
  public var brandName: String?
  public var price: Float?
  public var buyPriceWithoutCommission: Float?

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
    productId = json[SerializationKeys.productId].int
    buyPrice = json[SerializationKeys.buyPrice].float
    id = json[SerializationKeys.id].int
    productName = json[SerializationKeys.productName].string
    orderId = json[SerializationKeys.orderId].string
    stateName = json[SerializationKeys.stateName].string
    image = json[SerializationKeys.image].string
    size = json[SerializationKeys.size].string
    needSaleConfirm = json[SerializationKeys.needSaleConfirm].boolValue
    brandName = json[SerializationKeys.brandName].string
    price = json[SerializationKeys.price].float
    buyPriceWithoutCommission = json[SerializationKeys.buyPriceWithoutCommission].float
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = productId { dictionary[SerializationKeys.productId] = value }
    if let value = buyPrice { dictionary[SerializationKeys.buyPrice] = value }
    if let value = id { dictionary[SerializationKeys.id] = value }
    if let value = productName { dictionary[SerializationKeys.productName] = value }
    if let value = orderId { dictionary[SerializationKeys.orderId] = value }
    if let value = stateName { dictionary[SerializationKeys.stateName] = value }
    if let value = image { dictionary[SerializationKeys.image] = value }
    if let value = size { dictionary[SerializationKeys.size] = value }
    dictionary[SerializationKeys.needSaleConfirm] = needSaleConfirm
    if let value = brandName { dictionary[SerializationKeys.brandName] = value }
    if let value = price { dictionary[SerializationKeys.price] = value }
    if let value = buyPriceWithoutCommission { dictionary[SerializationKeys.buyPriceWithoutCommission] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.productId = aDecoder.decodeObject(forKey: SerializationKeys.productId) as? Int
    self.buyPrice = aDecoder.decodeObject(forKey: SerializationKeys.buyPrice) as? Float
    self.id = aDecoder.decodeObject(forKey: SerializationKeys.id) as? Int
    self.productName = aDecoder.decodeObject(forKey: SerializationKeys.productName) as? String
    self.orderId = aDecoder.decodeObject(forKey: SerializationKeys.orderId) as? String
    self.stateName = aDecoder.decodeObject(forKey: SerializationKeys.stateName) as? String
    self.image = aDecoder.decodeObject(forKey: SerializationKeys.image) as? String
    self.size = aDecoder.decodeObject(forKey: SerializationKeys.size) as? String
    self.needSaleConfirm = aDecoder.decodeBool(forKey: SerializationKeys.needSaleConfirm)
    self.brandName = aDecoder.decodeObject(forKey: SerializationKeys.brandName) as? String
    self.price = aDecoder.decodeObject(forKey: SerializationKeys.price) as? Float
    self.buyPriceWithoutCommission = aDecoder.decodeObject(forKey: SerializationKeys.buyPriceWithoutCommission) as? Float
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(productId, forKey: SerializationKeys.productId)
    aCoder.encode(buyPrice, forKey: SerializationKeys.buyPrice)
    aCoder.encode(id, forKey: SerializationKeys.id)
    aCoder.encode(productName, forKey: SerializationKeys.productName)
    aCoder.encode(orderId, forKey: SerializationKeys.orderId)
    aCoder.encode(stateName, forKey: SerializationKeys.stateName)
    aCoder.encode(image, forKey: SerializationKeys.image)
    aCoder.encode(size, forKey: SerializationKeys.size)
    aCoder.encode(needSaleConfirm, forKey: SerializationKeys.needSaleConfirm)
    aCoder.encode(brandName, forKey: SerializationKeys.brandName)
    aCoder.encode(price, forKey: SerializationKeys.price)
    aCoder.encode(buyPriceWithoutCommission, forKey: SerializationKeys.buyPriceWithoutCommission)
  }
}
