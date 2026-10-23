package com.example.trandify1.data

import com.example.trandify1.data.model.Category
import com.example.trandify1.data.model.Product

object SampleData {

    val bannerSlides = listOf(
        BannerSlide(
            id = "b1",
            title = "ARTICLE REIMAGINED",
            subtitle = "Autumn Minimalist Collection",
            imageUrl = ImageUrls.BANNER_1,
            isLive = true
        ),
        BannerSlide(
            id = "b2",
            title = "STREETWEAR ERA",
            subtitle = "Over-sized Silhouettes & Pastels",
            imageUrl = ImageUrls.BANNER_2,
            isLive = true
        ),
        BannerSlide(
            id = "b3",
            title = "LUXURY ESSENTIALS",
            subtitle = "Handcrafted Leather & Silk",
            imageUrl = ImageUrls.BANNER_3,
            isLive = false
        )
    )

    val categories = listOf(
        Category("cat_all", "All", ImageUrls.HOODIE_1, 24),
        Category("cat_women", "Women", ImageUrls.DRESS_1, 12),
        Category("cat_men", "Men", ImageUrls.JACKET_MEN, 10),
        Category("cat_kids", "Kids", ImageUrls.PINK_SWEATSHIRT, 6),
        Category("cat_shoes", "Shoes", ImageUrls.SNEAKERS_1, 8),
        Category("cat_bags", "Bags", ImageUrls.HANDBAG_1, 7),
        Category("cat_accessories", "Accessories", ImageUrls.SUNGLASSES_1, 15),
        Category("cat_sale", "Sale", ImageUrls.DENIM_SHORTS, 18)
    )

    val products = listOf(
        Product(
            id = "p1",
            title = "Oversized Charcoal Cotton Hoodie",
            brand = "Zara Minimal",
            description = "Premium heavy-weight fleece cotton hoodie with relaxed shoulders, kangaroo pocket, and ribbed trims.",
            price = 1299.00,
            originalPrice = 1699.00,
            discountPercentage = 20,
            rating = 4.8f,
            reviewCount = 124,
            category = "Women",
            imageUrls = listOf(ImageUrls.HOODIE_1, ImageUrls.PINK_SWEATSHIRT),
            isWishlisted = false
        ),
        Product(
            id = "p2",
            title = "Classic Crisp White Oxford Shirt",
            brand = "Myntra Studio",
            description = "Tailored 100% organic cotton white shirt with button-down collar and sleek modern cuffs.",
            price = 1499.00,
            originalPrice = 1899.00,
            discountPercentage = 20,
            rating = 4.6f,
            reviewCount = 98,
            category = "Women",
            imageUrls = listOf(ImageUrls.WHITE_SHIRT, ImageUrls.CASUAL_TEE),
            isWishlisted = true
        ),
        Product(
            id = "p3",
            title = "Soft Pastel Pink Sweatshirt",
            brand = "Trendify Essentials",
            description = "Ultra-soft cotton crewneck sweatshirt with plush lining for everyday comfort.",
            price = 1199.00,
            originalPrice = 1499.00,
            discountPercentage = 20,
            rating = 4.9f,
            reviewCount = 210,
            category = "Women",
            imageUrls = listOf(ImageUrls.PINK_SWEATSHIRT, ImageUrls.HOODIE_1),
            isWishlisted = false
        ),
        Product(
            id = "p4",
            title = "High-Rise Vintage Denim Shorts",
            brand = "Levi's Style",
            description = "Frayed hem vintage blue wash denim shorts with durable 5-pocket styling.",
            price = 1599.00,
            originalPrice = 1999.00,
            discountPercentage = 20,
            rating = 4.5f,
            reviewCount = 67,
            category = "Women",
            imageUrls = listOf(ImageUrls.DENIM_SHORTS, ImageUrls.DRESS_1),
            isWishlisted = false
        ),
        Product(
            id = "p5",
            title = "Chunky Retro White Sneakers",
            brand = "Nike Air Look",
            description = "Architectural lightweight chunky sole sneakers with breathable mesh panels.",
            price = 3499.00,
            originalPrice = 4399.00,
            discountPercentage = 20,
            rating = 4.9f,
            reviewCount = 340,
            category = "Shoes",
            imageUrls = listOf(ImageUrls.SNEAKERS_1, ImageUrls.RUNNING_SHOES),
            isWishlisted = true
        ),
        Product(
            id = "p6",
            title = "Retro Oversized UV Sunglasses",
            brand = "Ray-Look Eyewear",
            description = "Acetate full-rim UV400 protective dark tint lenses for modern streetwear aesthetic.",
            price = 899.00,
            originalPrice = 1199.00,
            discountPercentage = 20,
            rating = 4.7f,
            reviewCount = 89,
            category = "Accessories",
            imageUrls = listOf(ImageUrls.SUNGLASSES_1, ImageUrls.WATCH_1),
            isWishlisted = false
        ),
        Product(
            id = "p7",
            title = "Structured Leather Tote Handbag",
            brand = "Mango Luxe",
            description = "Genuine textured Italian leather shoulder handbag with magnetic clasp and internal zip compartment.",
            price = 3999.00,
            originalPrice = 4999.00,
            discountPercentage = 20,
            rating = 4.8f,
            reviewCount = 156,
            category = "Bags",
            imageUrls = listOf(ImageUrls.HANDBAG_1, ImageUrls.LEATHER_BAG),
            isWishlisted = false
        ),
        Product(
            id = "p8",
            title = "Floral Satin Midi Wrap Dress",
            brand = "Zara Woman",
            description = "Flowy A-line wrap midi dress with subtle side slit and adjustable waist tie.",
            price = 2299.00,
            originalPrice = 2899.00,
            discountPercentage = 20,
            rating = 4.7f,
            reviewCount = 112,
            category = "Women",
            imageUrls = listOf(ImageUrls.DRESS_1, ImageUrls.OVERSIZED_BLAZER),
            isWishlisted = false
        ),
        Product(
            id = "p9",
            title = "Suede Biker Jacket Charcoal",
            brand = "Zara Men",
            description = "Heavy duty suede finish asymmetrical zip leather biker jacket with silver hardware.",
            price = 4999.00,
            originalPrice = 6299.00,
            discountPercentage = 20,
            rating = 4.9f,
            reviewCount = 78,
            category = "Men",
            imageUrls = listOf(ImageUrls.JACKET_MEN, ImageUrls.CASUAL_TEE),
            isWishlisted = false
        ),
        Product(
            id = "p10",
            title = "Minimalist Minimal Dial Watch",
            brand = "Fossil Style",
            description = "Black stainless steel case with genuine dark brown leather strap and Japanese quartz movement.",
            price = 2999.00,
            originalPrice = 3799.00,
            discountPercentage = 20,
            rating = 4.6f,
            reviewCount = 94,
            category = "Accessories",
            imageUrls = listOf(ImageUrls.WATCH_1, ImageUrls.SUNGLASSES_1),
            isWishlisted = false
        ),
        Product(
            id = "p11",
            title = "Structured Crossbody Satchel Bag",
            brand = "Michael Luxe",
            description = "Sleek compact crossbody satchel with detachable chain link strap.",
            price = 3299.00,
            originalPrice = 4199.00,
            discountPercentage = 20,
            rating = 4.8f,
            reviewCount = 180,
            category = "Bags",
            imageUrls = listOf(ImageUrls.LEATHER_BAG, ImageUrls.HANDBAG_1),
            isWishlisted = false
        ),
        Product(
            id = "p12",
            title = "Breathable Performance Running Shoes",
            brand = "Nike Sport",
            description = "Engineered flyknit upper with responsive foam cushioning for ultimate stride comfort.",
            price = 4299.00,
            originalPrice = 5399.00,
            discountPercentage = 20,
            rating = 4.9f,
            reviewCount = 420,
            category = "Shoes",
            imageUrls = listOf(ImageUrls.RUNNING_SHOES, ImageUrls.SNEAKERS_1),
            isWishlisted = false
        ),
        Product(
            id = "p13",
            title = "Organic Cotton Oversized Tee",
            brand = "Uniqlo Look",
            description = "Pre-shrunk thick heavyweight cotton drop-shoulder casual daily tee.",
            price = 799.00,
            originalPrice = 999.00,
            discountPercentage = 20,
            rating = 4.5f,
            reviewCount = 88,
            category = "Men",
            imageUrls = listOf(ImageUrls.CASUAL_TEE, ImageUrls.WHITE_SHIRT),
            isWishlisted = false
        ),
        Product(
            id = "p14",
            title = "Tailored Single-Breasted Blazer",
            brand = "H&M Premium",
            description = "Sharp structured shoulder tailored blazer perfect for smart casual styling.",
            price = 3199.00,
            originalPrice = 3999.00,
            discountPercentage = 20,
            rating = 4.7f,
            reviewCount = 105,
            category = "Women",
            imageUrls = listOf(ImageUrls.OVERSIZED_BLAZER, ImageUrls.DRESS_1),
            isWishlisted = false
        ),
        Product(
            id = "p15",
            title = "Wool Blend Longline Trench Coat",
            brand = "Zara Edition",
            description = "Classic double-breasted long trench coat with storm flap and belt loop tie.",
            price = 5999.00,
            originalPrice = 7499.00,
            discountPercentage = 20,
            rating = 4.9f,
            reviewCount = 142,
            category = "Women",
            imageUrls = listOf(ImageUrls.WINTER_COAT, ImageUrls.JACKET_MEN),
            isWishlisted = false
        ),
        Product(
            id = "p16",
            title = "Woven Straw Wide-Brim Sun Hat",
            brand = "Beach Lifestyle",
            description = "100% natural straw hand-woven floppy hat with black ribbon detail.",
            price = 999.00,
            originalPrice = 1299.00,
            discountPercentage = 20,
            rating = 4.4f,
            reviewCount = 52,
            category = "Accessories",
            imageUrls = listOf(ImageUrls.SUMMER_HAT, ImageUrls.SUNGLASSES_1),
            isWishlisted = false
        ),
        Product(
            id = "p17",
            title = "Kids Soft Cotton Graphic Hoodie",
            brand = "Trendify Kids",
            description = "Durable fleece interior hoodie with elasticated cuffs and tagless neckline.",
            price = 999.00,
            originalPrice = 1299.00,
            discountPercentage = 20,
            rating = 4.8f,
            reviewCount = 45,
            category = "Kids",
            imageUrls = listOf(ImageUrls.PINK_SWEATSHIRT, ImageUrls.HOODIE_1),
            isWishlisted = false
        ),
        Product(
            id = "p18",
            title = "Kids Denim Overalls Jumpsuit",
            brand = "Gap Kids Look",
            description = "Comfortable stretch denim overalls with adjustable metal buckle straps.",
            price = 1299.00,
            originalPrice = 1599.00,
            discountPercentage = 20,
            rating = 4.7f,
            reviewCount = 38,
            category = "Kids",
            imageUrls = listOf(ImageUrls.DENIM_SHORTS, ImageUrls.WHITE_SHIRT),
            isWishlisted = false
        ),
        Product(
            id = "p19",
            title = "Urban Water-Resistant Backpack",
            brand = "Herschel Style",
            description = "Padded 15-inch laptop compartment with water-resistant polyester exterior.",
            price = 1999.00,
            originalPrice = 2499.00,
            discountPercentage = 20,
            rating = 4.8f,
            reviewCount = 230,
            category = "Bags",
            imageUrls = listOf(ImageUrls.HANDBAG_1, ImageUrls.LEATHER_BAG),
            isWishlisted = false
        ),
        Product(
            id = "p20",
            title = "Minimalist Stainless Steel Cuff",
            brand = "Maison Jewellery",
            description = "Polished 18k gold plated stainless steel open cuff bracelet.",
            price = 1199.00,
            originalPrice = 1499.00,
            discountPercentage = 20,
            rating = 4.6f,
            reviewCount = 71,
            category = "Accessories",
            imageUrls = listOf(ImageUrls.WATCH_1, ImageUrls.SUNGLASSES_1),
            isWishlisted = false
        )
    )
}

data class BannerSlide(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val isLive: Boolean
)