//
//  ProductRequest.swift
//
//  Created by Виталий Хлудеев on 03.12.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON

public final class ProductRequest: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let state = "state"
    static let seller = "seller"
    static let page = "page"
    static let vintage = "vintage"
    static let sort = "sort"
    static let startPrice = "startPrice"
    static let sizeType = "sizeType"
    static let size = "size"
    static let onSale = "onSale"
    static let category = "category"
    static let brand = "brand"
    static let productCondition = "productCondition"
    static let newCollection = "newCollection"
    static let filter = "filter"
    static let ourChoice = "ourChoice"
    static let productState = "productState"
    static let endPrice = "endPrice"
  }

  // MARK: Properties
  public var state: String?
  public var seller: Int?
  public var page: Int?
  public var vintage: Bool? = false
  public var sort: String?
  public var startPrice: Float?
  public var sizeType: String?
  public var size: [Int]! = []
  public var onSale: Bool? = false
  public var category: Int?
  public var brand: [Int]! = []
  public var productCondition: [Int]! = []
  public var newCollection: Bool? = false
  public var filter: [Int]! = []
  public var ourChoice: Bool? = false
  public var productState: [Int]! = []
  public var endPrice: Float?

  init(){}

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
    state = json[SerializationKeys.state].string
    seller = json[SerializationKeys.seller].int
    page = json[SerializationKeys.page].int
    vintage = json[SerializationKeys.vintage].boolValue
    sort = json[SerializationKeys.sort].string
    startPrice = json[SerializationKeys.startPrice].float
    sizeType = json[SerializationKeys.sizeType].string
    if let items = json[SerializationKeys.size].array { size = items.map { $0.intValue } }
    onSale = json[SerializationKeys.onSale].boolValue
    category = json[SerializationKeys.category].int
    if let items = json[SerializationKeys.brand].array { brand = items.map { $0.intValue } }
    if let items = json[SerializationKeys.productCondition].array { productCondition = items.map { $0.intValue } }
    newCollection = json[SerializationKeys.newCollection].boolValue
    if let items = json[SerializationKeys.filter].array { filter = items.map { $0.intValue } }
    ourChoice = json[SerializationKeys.ourChoice].boolValue
    if let items = json[SerializationKeys.productState].array { productState = items.map { $0.intValue } }
    endPrice = json[SerializationKeys.endPrice].float
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = state { dictionary[SerializationKeys.state] = value }
    if let value = seller { dictionary[SerializationKeys.seller] = value }
    if let value = page { dictionary[SerializationKeys.page] = value }
    dictionary[SerializationKeys.vintage] = vintage
    if let value = sort { dictionary[SerializationKeys.sort] = value }
    if let value = startPrice { dictionary[SerializationKeys.startPrice] = value }
    if let value = sizeType { dictionary[SerializationKeys.sizeType] = value }
    if let value = size { dictionary[SerializationKeys.size] = value.map({String($0)}).joined(separator: ",") } // сервер принимает массив чисел через запятую
    dictionary[SerializationKeys.onSale] = onSale
    if let value = category { dictionary[SerializationKeys.category] = value }
    if let value = brand { dictionary[SerializationKeys.brand] = value.map({String($0)}).joined(separator: ",") } // сервер принимает массив чисел через запятую
    if let value = productCondition { dictionary[SerializationKeys.productCondition] = value.map({String($0)}).joined(separator: ",") } // сервер принимает массив чисел через запятую
    dictionary[SerializationKeys.newCollection] = newCollection
    if let value = filter { dictionary[SerializationKeys.filter] = value.map({String($0)}).joined(separator: ",") } // сервер принимает массив чисел через запятую
    dictionary[SerializationKeys.ourChoice] = ourChoice
    if let value = productState { dictionary[SerializationKeys.productState] = value.map({String($0)}).joined(separator: ",") } // сервер принимает массив чисел через запятую
    if let value = endPrice { dictionary[SerializationKeys.endPrice] = value }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.state = aDecoder.decodeObject(forKey: SerializationKeys.state) as? String
    self.seller = aDecoder.decodeObject(forKey: SerializationKeys.seller) as? Int
    self.page = aDecoder.decodeObject(forKey: SerializationKeys.page) as? Int
    self.vintage = aDecoder.decodeBool(forKey: SerializationKeys.vintage)
    self.sort = aDecoder.decodeObject(forKey: SerializationKeys.sort) as? String
    self.startPrice = aDecoder.decodeObject(forKey: SerializationKeys.startPrice) as? Float
    self.sizeType = aDecoder.decodeObject(forKey: SerializationKeys.sizeType) as? String
    self.size = aDecoder.decodeObject(forKey: SerializationKeys.size) as? [Int]
    self.onSale = aDecoder.decodeBool(forKey: SerializationKeys.onSale)
    self.category = aDecoder.decodeObject(forKey: SerializationKeys.category) as? Int
    self.brand = aDecoder.decodeObject(forKey: SerializationKeys.brand) as? [Int]
    self.productCondition = aDecoder.decodeObject(forKey: SerializationKeys.productCondition) as? [Int]
    self.newCollection = aDecoder.decodeBool(forKey: SerializationKeys.newCollection)
    self.filter = aDecoder.decodeObject(forKey: SerializationKeys.filter) as? [Int]
    self.ourChoice = aDecoder.decodeBool(forKey: SerializationKeys.ourChoice)
    self.productState = aDecoder.decodeObject(forKey: SerializationKeys.productState) as? [Int]
    self.endPrice = aDecoder.decodeObject(forKey: SerializationKeys.endPrice) as? Float
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(state, forKey: SerializationKeys.state)
    aCoder.encode(seller, forKey: SerializationKeys.seller)
    aCoder.encode(page, forKey: SerializationKeys.page)
    aCoder.encode(vintage, forKey: SerializationKeys.vintage)
    aCoder.encode(sort, forKey: SerializationKeys.sort)
    aCoder.encode(startPrice, forKey: SerializationKeys.startPrice)
    aCoder.encode(sizeType, forKey: SerializationKeys.sizeType)
    aCoder.encode(size, forKey: SerializationKeys.size)
    aCoder.encode(onSale, forKey: SerializationKeys.onSale)
    aCoder.encode(category, forKey: SerializationKeys.category)
    aCoder.encode(brand, forKey: SerializationKeys.brand)
    aCoder.encode(productCondition, forKey: SerializationKeys.productCondition)
    aCoder.encode(newCollection, forKey: SerializationKeys.newCollection)
    aCoder.encode(filter, forKey: SerializationKeys.filter)
    aCoder.encode(ourChoice, forKey: SerializationKeys.ourChoice)
    aCoder.encode(productState, forKey: SerializationKeys.productState)
    aCoder.encode(endPrice, forKey: SerializationKeys.endPrice)
  }

}
