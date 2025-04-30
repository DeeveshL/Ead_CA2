using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using EAD2_CA2_POKEMON.Models;

[ApiController]
[Route("api/users/{UserId}/collections")]
public class CollectionsController : ControllerBase
{
    private readonly AppDbContext _context;

    public CollectionsController(AppDbContext context)
    {
        _context = context;
    }

    // POST: api/users/{UserId}/collections
    [HttpPost]
    public async Task<IActionResult> CreateCollection(string UserId, [FromBody] Collection collection)
    {
        bool exists = await _context.Collections.AnyAsync(c =>
            c.UserId == UserId &&
            c.Name == collection.Name);

        if (exists)
            return Conflict("Collection already exists");

        collection.Id = Guid.NewGuid();
        collection.UserId = UserId;

        _context.Collections.Add(collection);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetCollection), new { UserId, collectionName = collection.Name }, collection);
    }

    // GET: api/users/{UserId}/collections
    [HttpGet]
    public async Task<ActionResult<IEnumerable<Collection>>> GetCollections(string UserId)
    {
        return await _context.Collections
            .Where(c => c.UserId == UserId)
            .ToListAsync();
    }

    // GET: api/users/{UserId}/collections/{collectionName}
    [HttpGet("{collectionName}")]
    public async Task<ActionResult<Collection>> GetCollection(string UserId, string collectionName)
    {
        var collection = await _context.Collections
            .Include(c => c.CollectionCards)
            .ThenInclude(cc => cc.Card)
            .FirstOrDefaultAsync(c =>
                c.UserId == UserId &&
                c.Name == collectionName);

        if (collection == null) return NotFound();
        return collection;
    }

    // POST: api/users/{UserId}/collections/{collectionName}/cards
    [HttpPost("{collectionName}/cards")]
    public async Task<IActionResult> AddCardToCollection(string UserId, string collectionName, [FromBody] Guid cardId)
    {
        var collection = await _context.Collections.FirstOrDefaultAsync(c =>
            c.UserId == UserId && c.Name == collectionName);

        if (collection == null)
            return NotFound("Collection not found");

        bool alreadyExists = await _context.CollectionCards.AnyAsync(cc =>
            cc.UserId == UserId &&
            cc.CollectionId == collection.Id &&
            cc.CardId == cardId);

        if (alreadyExists)
            return Conflict("Card already in collection");

        var join = new CollectionCard
        {
            UserId = UserId,
            CollectionId = collection.Id,
            CardId = cardId
        };

        _context.CollectionCards.Add(join);
        await _context.SaveChangesAsync();

        return Ok("Card added");
    }
}
