namespace EAD2_CA2_POKEMON.Models;

public class Collection
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public string Name { get; set; } = string.Empty;
    public string UserId { get; set; } = string.Empty;

    public List<CollectionCard> CollectionCards { get; set; } = new();
}
