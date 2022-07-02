//
//  Draft.swift
//
//  Created by Виталий Хлудеев on 20.10.17
//  Copyright (c) . All rights reserved.
//

import Foundation
import SwiftyJSON
import CoreFoundation

public final class Draft: NSCoding {

  // MARK: Declaration for string constants to be used to decode and also serialize.
  private struct SerializationKeys {
    static let purchasePrice = "purchasePrice"
    static let categoryName = "categoryName"
    static let sellerRequisite = "sellerRequisite"
    static let serialNumber = "serialNumber"
    static let commission = "commission"
    static let vintage = "vintage"
    static let descriptionValue = "description"
    static let brandName = "brandName"
    static let model = "model"
    static let category = "category"
    static let brand = "brand"
    static let selectedAttributeValues = "selectedAttributeValues"
    static let origin = "origin"
    static let priceWithoutCommission = "priceWithoutCommission"
    static let priceWithCommission = "priceWithCommission"
    static let id = "id"
    static let purchaseYear = "purchaseYear"
    static let selectedSizeType = "selectedSizeType"
    static let selectedCategories = "selectedCategories"
    static let images = "images"
    static let selectedSize = "selectedSize"
    static let selectedCondition = "selectedCondition"
    static let samples = "samples"
    static let completedSteps = "completedSteps"
  }

  // MARK: Properties
  public var purchasePrice: Float?
  public var categoryName: String?
  public var sellerRequisite: SellerRequisite?
  public var serialNumber: String?
  public var commission: Float?
  public var vintage: Bool? = false
  public var descriptionValue: String?
  public var brandName: String?
  public var model: String?
  public var category: Int?
  public var brand: Int?
  public var selectedAttributeValues: [SelectedAttributeValues]?
  public var origin: String?
  public var priceWithoutCommission: Float?
  public var priceWithCommission: Float?
  public var id: Int?
  public var purchaseYear: Int?
  public var selectedSizeType: String?
  public var selectedCategories: [Int]?
  public var completedSteps: [Int]! = []
  public var images: [Images]?
  public var selectedSize: SelectedSize?
  public var selectedCondition: Int?
  public var samples: [PublicationPhotoSample]?

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
    purchasePrice = json[SerializationKeys.purchasePrice].float
    categoryName = json[SerializationKeys.categoryName].string
    sellerRequisite = SellerRequisite(json: json[SerializationKeys.sellerRequisite])
    serialNumber = json[SerializationKeys.serialNumber].string
    commission = json[SerializationKeys.commission].float
    vintage = json[SerializationKeys.vintage].boolValue
    descriptionValue = json[SerializationKeys.descriptionValue].string
    brandName = json[SerializationKeys.brandName].string
    model = json[SerializationKeys.model].string
    category = json[SerializationKeys.category].int
    brand = json[SerializationKeys.brand].int
    if let items = json[SerializationKeys.selectedAttributeValues].array { selectedAttributeValues = items.map { SelectedAttributeValues(json: $0) } }
    origin = json[SerializationKeys.origin].string
    priceWithoutCommission = json[SerializationKeys.priceWithoutCommission].float
    priceWithCommission = json[SerializationKeys.priceWithCommission].float
    id = json[SerializationKeys.id].int
    purchaseYear = json[SerializationKeys.purchaseYear].int
    selectedSizeType = json[SerializationKeys.selectedSizeType].string
    if let items = json[SerializationKeys.selectedCategories].array { selectedCategories = items.map { $0.intValue } }
    if let items = json[SerializationKeys.completedSteps].array { completedSteps = items.map { $0.intValue } }
    if let items = json[SerializationKeys.images].array { images = items.map { Images(json: $0) } }
    selectedSize = SelectedSize(json: json[SerializationKeys.selectedSize])
    selectedCondition = json[SerializationKeys.selectedCondition].int
    if let items = json[SerializationKeys.samples].array { samples = items.map { PublicationPhotoSample(json: $0) } }
  }

  /// Generates description of the object in the form of a NSDictionary.
  ///
  /// - returns: A Key value pair containing all valid values in the object.
  public func dictionaryRepresentation() -> [String: Any] {
    var dictionary: [String: Any] = [:]
    if let value = purchasePrice { dictionary[SerializationKeys.purchasePrice] = value }
    if let value = categoryName { dictionary[SerializationKeys.categoryName] = value }
    if let value = sellerRequisite { dictionary[SerializationKeys.sellerRequisite] = value.dictionaryRepresentation() }
    if let value = serialNumber { dictionary[SerializationKeys.serialNumber] = value }
    if let value = commission { dictionary[SerializationKeys.commission] = value }
    dictionary[SerializationKeys.vintage] = vintage
    if let value = descriptionValue { dictionary[SerializationKeys.descriptionValue] = value }
    if let value = brandName { dictionary[SerializationKeys.brandName] = value }
    if let value = model { dictionary[SerializationKeys.model] = value }
    if let value = category { dictionary[SerializationKeys.category] = value }
    if let value = brand { dictionary[SerializationKeys.brand] = value }
    if let value = selectedAttributeValues { dictionary[SerializationKeys.selectedAttributeValues] = value.map { $0.dictionaryRepresentation() } }
    if let value = origin { dictionary[SerializationKeys.origin] = value }
    if let value = priceWithoutCommission { dictionary[SerializationKeys.priceWithoutCommission] = value }
    if let value = priceWithCommission { dictionary[SerializationKeys.priceWithCommission] = value }
    if let value = id { dictionary[SerializationKeys.id] = value }
    if let value = purchaseYear { dictionary[SerializationKeys.purchaseYear] = value }
    if let value = selectedSizeType { dictionary[SerializationKeys.selectedSizeType] = value }
    if let value = selectedCategories { dictionary[SerializationKeys.selectedCategories] = value }
    if let value = completedSteps { dictionary[SerializationKeys.completedSteps] = value }
    if let value = images { dictionary[SerializationKeys.images] = value.map { $0.dictionaryRepresentation() } }
    if let value = selectedSize { dictionary[SerializationKeys.selectedSize] = value.dictionaryRepresentation() }
    if let value = selectedCondition { dictionary[SerializationKeys.selectedCondition] = value }
    if let value = samples { dictionary[SerializationKeys.samples] = value.map { $0.dictionaryRepresentation() } }
    return dictionary
  }

  // MARK: NSCoding Protocol
  required public init(coder aDecoder: NSCoder) {
    self.purchasePrice = aDecoder.decodeObject(forKey: SerializationKeys.purchasePrice) as? Float
    self.categoryName = aDecoder.decodeObject(forKey: SerializationKeys.categoryName) as? String
    self.sellerRequisite = aDecoder.decodeObject(forKey: SerializationKeys.sellerRequisite) as? SellerRequisite
    self.serialNumber = aDecoder.decodeObject(forKey: SerializationKeys.serialNumber) as? String
    self.commission = aDecoder.decodeObject(forKey: SerializationKeys.commission) as? Float
    self.vintage = aDecoder.decodeBool(forKey: SerializationKeys.vintage)
    self.descriptionValue = aDecoder.decodeObject(forKey: SerializationKeys.descriptionValue) as? String
    self.brandName = aDecoder.decodeObject(forKey: SerializationKeys.brandName) as? String
    self.model = aDecoder.decodeObject(forKey: SerializationKeys.model) as? String
    self.category = aDecoder.decodeObject(forKey: SerializationKeys.category) as? Int
    self.brand = aDecoder.decodeObject(forKey: SerializationKeys.brand) as? Int
    self.selectedAttributeValues = aDecoder.decodeObject(forKey: SerializationKeys.selectedAttributeValues) as? [SelectedAttributeValues]
    self.origin = aDecoder.decodeObject(forKey: SerializationKeys.origin) as? String
    self.priceWithoutCommission = aDecoder.decodeObject(forKey: SerializationKeys.priceWithoutCommission) as? Float
    self.priceWithCommission = aDecoder.decodeObject(forKey: SerializationKeys.priceWithCommission) as? Float
    self.id = aDecoder.decodeObject(forKey: SerializationKeys.id) as? Int
    self.purchaseYear = aDecoder.decodeObject(forKey: SerializationKeys.purchaseYear) as? Int
    self.selectedSizeType = aDecoder.decodeObject(forKey: SerializationKeys.selectedSizeType) as? String
    self.selectedCategories = aDecoder.decodeObject(forKey: SerializationKeys.selectedCategories) as? [Int]
    self.completedSteps = aDecoder.decodeObject(forKey: SerializationKeys.completedSteps) as? [Int]
    self.images = aDecoder.decodeObject(forKey: SerializationKeys.images) as? [Images]
    self.selectedSize = aDecoder.decodeObject(forKey: SerializationKeys.selectedSize) as? SelectedSize
    self.selectedCondition = aDecoder.decodeObject(forKey: SerializationKeys.selectedCondition) as? Int
    self.samples = aDecoder.decodeObject(forKey: SerializationKeys.samples) as? [PublicationPhotoSample]
  }

  public func encode(with aCoder: NSCoder) {
    aCoder.encode(purchasePrice, forKey: SerializationKeys.purchasePrice)
    aCoder.encode(categoryName, forKey: SerializationKeys.categoryName)
    aCoder.encode(sellerRequisite, forKey: SerializationKeys.sellerRequisite)
    aCoder.encode(serialNumber, forKey: SerializationKeys.serialNumber)
    aCoder.encode(commission, forKey: SerializationKeys.commission)
    aCoder.encode(vintage, forKey: SerializationKeys.vintage)
    aCoder.encode(descriptionValue, forKey: SerializationKeys.descriptionValue)
    aCoder.encode(brandName, forKey: SerializationKeys.brandName)
    aCoder.encode(model, forKey: SerializationKeys.model)
    aCoder.encode(category, forKey: SerializationKeys.category)
    aCoder.encode(brand, forKey: SerializationKeys.brand)
    aCoder.encode(selectedAttributeValues, forKey: SerializationKeys.selectedAttributeValues)
    aCoder.encode(origin, forKey: SerializationKeys.origin)
    aCoder.encode(priceWithoutCommission, forKey: SerializationKeys.priceWithoutCommission)
    aCoder.encode(id, forKey: SerializationKeys.id)
    aCoder.encode(purchaseYear, forKey: SerializationKeys.purchaseYear)
    aCoder.encode(selectedSizeType, forKey: SerializationKeys.selectedSizeType)
    aCoder.encode(selectedCategories, forKey: SerializationKeys.selectedCategories)
    aCoder.encode(completedSteps, forKey: SerializationKeys.completedSteps)
    aCoder.encode(images, forKey: SerializationKeys.images)
    aCoder.encode(selectedSize, forKey: SerializationKeys.selectedSize)
    aCoder.encode(selectedCondition, forKey: SerializationKeys.selectedCondition)
    aCoder.encode(samples, forKey: SerializationKeys.samples)
  }

}
